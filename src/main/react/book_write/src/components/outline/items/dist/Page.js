"use strict";
exports.__esModule = true;
var react_1 = require("react");
var react_router_dom_1 = require("react-router-dom");
function Page(_a) {
    var chapterId = _a.chapterId, page = _a.page;
    return (react_1["default"].createElement("li", null,
        react_1["default"].createElement(react_router_dom_1.Link, { to: "/chapter/" + chapterId + "/page/" + page.id, className: "flex bg-gray-800 p-1 items-center pl-6 w-full text-white text-sm font-normal text-gray-900 rounded-lg transition duration-75 group hover:bg-gray-700" }, page.title)));
}
exports["default"] = Page;
