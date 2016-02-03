import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Error 1
 *
 *      Exception in thread "main" org.mozilla.javascript.EcmaError: TypeError: Cannot call method "slice" of null (sass.sync.js#74)
 *
 * Fix: See https://github.com/kripken/emscripten/issues/2601
 *
 * Replace
 *      var sourceRegex=/^function\s*\(([^)]*)\)\s*{\s*([^*]*?)[\s;]*(?:return\s*(.*?)[;\s]*)?}$/;
 * with
 *      var sourceRegex=/^function\s*\(([^)]*)\)\s*{\s*([^*]*?)[\s;]*(?:return\s*(.*?)[;\s]*)?}$/m;
 *
 *
 * Error 2
 *
 *      Exception in thread "main" org.mozilla.javascript.JavaScriptException: abort("Assertion failed: Typed arrays 2 must be run on a little-endian system") at (no stack trace available)
 *      Assertion failed: Typed arrays 2 must be run on a little-endian system
 *      Assertion failed: Typed arrays 2 must be run on a little-endian system
 *      If this abort() is unexpected, build with -s ASSERTIONS=1 which can give more information. (sass.sync.js#97)
 *
 * Fix: Don't know
 */
public class TestRhino {

    private static final String SHIMS = "rhino-shims.js";
    private static final String SASS = "test-safe-split-memory_sass.sync.js";

    public static void main(String[] args) throws IOException {
        final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();

        final Context context = Context.enter();
        context.setOptimizationLevel(-1);
        // needed for typed arrays
        context.setLanguageVersion(Context.VERSION_ES6);

        final Scriptable globalScope = context.initStandardObjects();

        final Reader shimsReader = new InputStreamReader(contextClassLoader.getResourceAsStream(SHIMS));
        context.evaluateReader(globalScope, shimsReader, SHIMS, 1, null);

        final Reader sassSyncJsReader = new InputStreamReader(contextClassLoader.getResourceAsStream(SASS));
        context.evaluateReader(globalScope, sassSyncJsReader, SASS, 1, null);

        final String sass = "var scss = '$someVar: 123px; .some-selector { width: $someVar; }';\n" +
                "Sass.compile(scss, function(result) {\n" +
                "    console.log(result.text);\n" +
                "});";
        context.evaluateString(globalScope, sass, "sass", 1, null);

        Context.exit();
    }
}
