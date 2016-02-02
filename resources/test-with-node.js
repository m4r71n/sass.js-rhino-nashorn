Sass = require('./sass.sync.js');
var scss = '$someVar: 123px; .some-selector { width: $someVar; }';
Sass.compile(scss, function(result) {
  console.log(result.text);
});