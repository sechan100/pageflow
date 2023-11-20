"use strict";

var _react = _interopRequireDefault(require("react"));

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { "default": obj }; }

exports.__esModule = true;

var react_quill_1 = require("react-quill");

require("react-quill/dist/quill.snow.css");

function QuillEditor() {
  var _a = _react["default"].useState(""),
      quillValue = _a[0],
      setQuillValue = _a[1];

  return _react["default"]["default"].createElement(_react["default"]["default"].Fragment, null, _react["default"]["default"].createElement(react_quill_1["default"], {
    theme: "snow",
    value: quillValue || ""
  }));
}

exports["default"] = QuillEditor;
;