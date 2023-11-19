"use strict";
exports.__esModule = true;
var react_router_dom_1 = require("react-router-dom");
var BookForm_1 = require("./pages/BookForm");
var MutationSaveBtn_1 = require("./MutationSaveBtn");
var ChapterForm_1 = require("./pages/ChapterForm");
function FormPage() {
    return (React.createElement("main", { className: "flex-auto relative" },
        React.createElement(MutationSaveBtn_1["default"], null),
        React.createElement(react_router_dom_1.Routes, null,
            React.createElement(react_router_dom_1.Route, { path: "/", element: React.createElement(BookForm_1["default"], null) }),
            React.createElement(react_router_dom_1.Route, { path: "/chapter/:chapterId", element: React.createElement(ChapterForm_1["default"], null) }))));
}
exports["default"] = FormPage;
