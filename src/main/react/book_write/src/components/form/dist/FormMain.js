"use strict";
var __assign = (this && this.__assign) || function () {
    __assign = Object.assign || function(t) {
        for (var s, i = 1, n = arguments.length; i < n; i++) {
            s = arguments[i];
            for (var p in s) if (Object.prototype.hasOwnProperty.call(s, p))
                t[p] = s[p];
        }
        return t;
    };
    return __assign.apply(this, arguments);
};
exports.__esModule = true;
var react_router_dom_1 = require("react-router-dom");
var ChapterForm_1 = require("./ChapterForm");
var BookForm_1 = require("./BookForm");
function FormPage(props) {
    return (React.createElement("main", { className: "flex-auto relative" },
        React.createElement(react_router_dom_1.Routes, null,
            React.createElement(react_router_dom_1.Route, { path: "/", element: React.createElement(BookForm_1["default"], __assign({}, props)) }),
            React.createElement(react_router_dom_1.Route, { path: "/chapter/:chapterId", element: React.createElement(ChapterForm_1["default"], __assign({}, props)) }))));
}
exports["default"] = FormPage;
