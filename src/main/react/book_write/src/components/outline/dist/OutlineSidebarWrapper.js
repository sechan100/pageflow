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
var __spreadArrays = (this && this.__spreadArrays) || function () {
    for (var s = 0, i = 0, il = arguments.length; i < il; i++) s += arguments[i].length;
    for (var r = Array(s), k = 0, i = 0; i < il; i++)
        for (var a = arguments[i], j = 0, jl = a.length; j < jl; j++, k++)
            r[k] = a[j];
    return r;
};
exports.__esModule = true;
function OutlineSidebarWrapper(props) {
    return (React.createElement(React.Fragment, null,
        React.createElement("aside", { id: "page-outline-sidebar", className: "fixed top-0 left-0 z-40 w-64 h-screen transition-transform -translate-x-full sm:translate-x-0" },
            React.createElement("div", { className: "overflow-y-auto pt-12 pb-5 px-3 h-full bg-white border-r border-gray-200 dark:bg-gray-800 dark:border-gray-700" }, props.children)),
        React.createElement(AddChapterBtn, { setBook: props.setBook }),
        React.createElement("div", { id: "sidebar-placeholder", className: "relative w-64 h-screen transition-transform -translate-x-full sm:translate-x-0" })));
}
exports["default"] = OutlineSidebarWrapper;
function AddChapterBtn(props) {
    function addChapter() {
        var newChapter = {
            id: Math.floor(Math.random() * 1000000000),
            title: "새 챕터",
            pages: []
        };
        props.setBook(function (prevBook) {
            return __assign(__assign({}, prevBook), { chapters: __spreadArrays(prevBook.chapters, [
                    newChapter
                ]) });
        });
    }
    return (React.createElement("button", { type: "button", onClick: addChapter, className: "fixed z-40 top-4 left-40 px-5 py-2 text-xs font-medium text-center ext-gray-900 focus:outline-none bg-white rounded-lg border border-gray-200 hover:bg-gray-100 hover:text-blue-700 dark:focus:ring-gray-700 dark:bg-gray-800 dark:text-gray-400 dark:border-gray-600 dark:hover:text-white dark:hover:bg-gray-700" }, "\uC0C8 \uCC55\uD130"));
}
