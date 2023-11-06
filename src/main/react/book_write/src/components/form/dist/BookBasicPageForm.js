"use strict";
exports.__esModule = true;
var react_1 = require("react");
var book_apis_1 = require("../../api/book-apis");
function BookBasicPageForm() {
    // function changeBookTitle(titleInput : any) {
    //   setBook({
    //     ...book,
    //     title: titleInput.target.value
    //   })
    // }
    // const book = dummyBook;
    var _a = book_apis_1.useGetBook(1), book = _a.data, isLoading = _a.isLoading, isFetching = _a.isFetching;
    if (isLoading) {
        return react_1["default"].createElement("div", null, "Loading...");
    }
    if (isFetching) {
        return react_1["default"].createElement("div", null, "Updating...");
    }
    return (react_1["default"].createElement(react_1["default"].Fragment, null,
        react_1["default"].createElement("form", { action: "#" },
            react_1["default"].createElement("div", { className: "sm:col-span-2" },
                react_1["default"].createElement("label", { htmlFor: "title", className: "block mb-2 text-sm font-medium text-gray-900" }, "\uCC45 \uCCB4\uBAA9"),
                react_1["default"].createElement("input", { value: book === null || book === void 0 ? void 0 : book.title, type: "text", name: "title", id: "title", className: "bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5", placeholder: "\uCC45 \uC81C\uBAA9\uC744 \uC785\uB825\uD574\uC8FC\uC138\uC694." })),
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
