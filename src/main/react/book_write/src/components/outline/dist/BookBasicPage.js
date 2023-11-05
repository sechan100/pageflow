"use strict";
exports.__esModule = true;
var book_apis_1 = require("../../api/book-apis");
function BookBasicPage(_a) {
    var bookId = _a.bookId;
    var _b = book_apis_1.useGetBook(bookId), data = _b.data, isLoading = _b.isLoading, isFetching = _b.isFetching, error = _b.error;
    var book = data;
    return (React.createElement("div", null,
        React.createElement("a", { href: "#", className: "flex items-center p-2 text-base font-normal text-gray-900 rounded-lg dark:text-white hover:bg-gray-100 dark:hover:bg-gray-700 group" },
            React.createElement("svg", { className: "w-6 h-6 text-gray-800 dark:text-white", xmlns: "http://www.w3.org/2000/svg", fill: "currentColor", viewBox: "0 0 16 20" },
                React.createElement("path", { d: "M16 14V2a2 2 0 0 0-2-2H2a2 2 0 0 0-2 2v15a3 3 0 0 0 3 3h12a1 1 0 0 0 0-2h-1v-2a2 2 0 0 0 2-2ZM4 2h2v12H4V2Zm8 16H3a1 1 0 0 1 0-2h9v2Z" })),
            React.createElement("span", { className: "ml-3 text-lg" }, book === null || book === void 0 ? void 0 : book.title))));
}
exports["default"] = BookBasicPage;
