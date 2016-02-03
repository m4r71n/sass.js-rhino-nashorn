import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.InputStreamReader;
import java.io.Reader;

public class TestNashorn {

    private static final String SHIMS = "nashorn-shims.js";
    private static final String SASS = "cleanup-initialization_sass.sync.js";

    public static void main(String[] args) throws ScriptException {
        final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();

        final ScriptEngine engine = new ScriptEngineManager().getEngineByName("Nashorn");

        final Reader shimsReader = new InputStreamReader(contextClassLoader.getResourceAsStream(SHIMS));
        engine.eval(shimsReader);

        final Reader sassSyncJsReader = new InputStreamReader(contextClassLoader.getResourceAsStream(SASS));
        engine.eval(sassSyncJsReader);

        final String sass = "var scss = '$someVar: 123px; .some-selector { width: $someVar; }';\n" +
                "Sass.compile(scss, function(result) {\n" +
                "    console.log(result.text);\n" +
                "});";
        engine.eval(sass);

        engine.eval("exit();");
    }
}
