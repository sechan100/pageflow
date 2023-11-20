"use strict";
exports.__esModule = true;
var react_1 = require("react");
var react_quill_1 = require("react-quill");
require("react-quill/dist/quill.snow.css");
function QuillEditor() {
    var _a = react_1.useState(""), quillValue = _a[0], setQuillValue = _a[1];
    react_1.useEffect(function () {
        console.log(quillValue);
    }, [quillValue]);
    var modules = {
        toolbar: [
            [{ header: [1, 2, 3, false] }],
            ["bold", "italic"],
            [{ "indent": "-1" }, { "indent": "+1" }],
            ["image"],
        ]
    };
    var formats = [
        "header",
        "bold",
        "italic",
        "indent",
        "image",
    ];
    return (react_1["default"].createElement(react_1["default"].Fragment, null,
        react_1["default"].createElement(react_quill_1["default"], { theme: "snow", modules: modules, formats: formats, value: quillValue, onChange: setQuillValue, preserveWhitespace: true, className: "h-1/2 overflow-y-auto" }),
        react_1["default"].createElement("div", { className: "border border-8 border-gray-800", dangerouslySetInnerHTML: { __html: quillValue } })));
}
exports["default"] = QuillEditor;
