"use strict";
exports.__esModule = true;
var NewChapterBtn_1 = require("./newItemBtn/NewChapterBtn");
function OutlineSidebar(_a) {
    var children = _a.children, localOutline = _a.outline;
    return (React.createElement("div", { className: 'flex' },
        React.createElement("aside", { id: "page-outline-sidebar", className: "fixed z-10 top-0 left-0 w-64 h-screen transition-transform -translate-x-full sm:translate-x-0" },
            React.createElement("div", { className: "overflow-y-auto pt-12 pb-5 px-3 h-full bg-white border-r border-gray-200 dark:bg-gray-800 dark:border-gray-700" }, children),
            React.createElement(NewChapterBtn_1["default"], { outline: localOutline })),
        React.createElement("div", { id: "sidebar-placeholder", className: "w-64 h-screen hidden sm:block" })));
}
exports["default"] = OutlineSidebar;
