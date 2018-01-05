/**
 * Created by Dmitri Rubinstein on 2017-12-04.
 */
// https://developer.mozilla.org/de/docs/Web/JavaScript/Reference/Global_Objects/String/startsWith
if (!String.prototype.startsWith) {
    String.prototype.startsWith = function(searchString, position) {
        position = position || 0;
        return this.indexOf(searchString, position) === position;
    };
}
