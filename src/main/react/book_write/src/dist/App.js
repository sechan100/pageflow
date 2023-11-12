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
require("./App.css");
var BookBasicPageForm_1 = require("./components/form/BookBasicPageForm");
var react_query_1 = require("react-query");
var OutlineSidebar_1 = require("./components/outline/OutlineSidebar");
var react_1 = require("react");
function App() {
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    var _a = react_1.useState(2), bookId = _a[0], setBookId = _a[1];
    var queryClient = new react_query_1.QueryClient({
        defaultOptions: {
            queries: {
                refetchOnMount: false,
                refetchOnWindowFocus: false,
                retry: false,
                staleTime: 1000 * 60 * 60 // 1시간
            }
        }
    });
    var drillingProps = {
        bookId: bookId,
        queryClient: queryClient
    };
    return (React.createElement(react_query_1.QueryClientProvider, { client: queryClient },
        React.createElement(OutlineSidebar_1["default"], __assign({}, drillingProps)),
        React.createElement("main", { className: "px-24 mt-16 flex-auto" },
            React.createElement(BookBasicPageForm_1["default"], __assign({}, drillingProps)))));
}
exports["default"] = App;
