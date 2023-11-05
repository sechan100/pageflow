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
var react_1 = require("react");
function BookBasicPageForm(_a) {
    var book = _a.book, setBook = _a.setBook;
    function changeBookTitle(titleInput) {
        setBook(__assign(__assign({}, book), { title: titleInput.target.value }));
    }
    return (react_1["default"].createElement(react_1["default"].Fragment, null,
        react_1["default"].createElement("form", { action: "#" },
            react_1["default"].createElement("div", { className: "sm:col-span-2" },
                react_1["default"].createElement("label", { htmlFor: "title", className: "block mb-2 text-sm font-medium text-gray-900" }, "\uCC45 \uCCB4\uBAA9"),
                react_1["default"].createElement("input", { value: book.title, onChange: changeBookTitle, type: "text", name: "title", id: "title", className: "bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5", placeholder: "\uCC45 \uC81C\uBAA9\uC744 \uC785\uB825\uD574\uC8FC\uC138\uC694." })),
            react_1["default"].createElement("div", { className: "w-full" },
                react_1["default"].createElement("label", { htmlFor: "brand", className: "block mb-2 text-sm font-medium text-gray-900" }, "Brand"),
                react_1["default"].createElement("input", { type: "text", name: "brand", id: "brand", className: "bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5", placeholder: "Product brand" })),
            react_1["default"].createElement("div", { className: "w-full" },
                react_1["default"].createElement("label", { htmlFor: "price", className: "block mb-2 text-sm font-medium text-gray-900" }, "Price"),
                react_1["default"].createElement("input", { type: "number", name: "price", id: "price", className: "bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5", placeholder: "$2999" })),
            react_1["default"].createElement("div", null,
                react_1["default"].createElement("label", { htmlFor: "item-weight", className: "block mb-2 text-sm font-medium text-gray-900" }, "Item Weight (kg)"),
                react_1["default"].createElement("input", { type: "number", name: "item-weight", id: "item-weight", className: "bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5", placeholder: "12" })),
            react_1["default"].createElement("button", { type: "submit", className: "inline-flex items-center px-5 py-2.5 mt-4 sm:mt-6 text-sm font-medium text-center text-white bg-primary-700 rounded-lg focus:ring-4 focus:ring-primary-200 hover:bg-primary-800" }, "Add product"))));
}
exports["default"] = BookBasicPageForm;
