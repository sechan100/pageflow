"use strict";
exports.__esModule = true;
var book_apis_1 = require("../../api/book-apis");
function BookBasicPageForm(drillingProps) {
    var bookId = drillingProps.bookId, queryClient = drillingProps.queryClient;
    var outline = book_apis_1.useGetOutline(bookId);
    return (React.createElement(React.Fragment, null,
        React.createElement("form", { action: "#" },
            React.createElement("div", { className: "sm:col-span-2" },
                React.createElement("label", { htmlFor: "title", className: "block mb-2 text-sm font-medium text-gray-900" }, "\uCC45 \uCCB4\uBAA9"),
                React.createElement("input", { value: outline.title, type: "text", name: "title", id: "title", className: "bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5", placeholder: "\uCC45 \uC81C\uBAA9\uC744 \uC785\uB825\uD574\uC8FC\uC138\uC694." })),
            React.createElement("div", { className: "w-full" },
                React.createElement("label", { htmlFor: "brand", className: "block mb-2 text-sm font-medium text-gray-900" }, "Brand"),
                React.createElement("input", { type: "text", name: "brand", id: "brand", className: "bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5", placeholder: "Product brand" })),
            React.createElement("div", { className: "w-full" },
                React.createElement("label", { htmlFor: "price", className: "block mb-2 text-sm font-medium text-gray-900" }, "Price"),
                React.createElement("input", { type: "number", name: "price", id: "price", className: "bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5", placeholder: "$2999" })),
            React.createElement("div", null,
                React.createElement("label", { htmlFor: "item-weight", className: "block mb-2 text-sm font-medium text-gray-900" }, "Item Weight (kg)"),
                React.createElement("input", { type: "number", name: "item-weight", id: "item-weight", className: "bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5", placeholder: "12" })),
            React.createElement("button", { type: "submit", className: "inline-flex items-center px-5 py-2.5 mt-4 sm:mt-6 text-sm font-medium text-center text-white bg-primary-700 rounded-lg focus:ring-4 focus:ring-primary-200 hover:bg-primary-800" }, "Add product"))));
}
exports["default"] = BookBasicPageForm;
