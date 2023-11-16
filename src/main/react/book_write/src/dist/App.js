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
exports.queryClient = void 0;
require("./App.css");
var react_query_1 = require("react-query");
var react_1 = require("react");
var BookEntityDraggableContext_1 = require("./components/BookEntityDraggableContext");
require("react-image-crop/dist/ReactCrop.css");
exports.queryClient = new react_query_1.QueryClient({
    defaultOptions: {
        queries: {
            refetchOnMount: false,
            refetchOnWindowFocus: false,
            retry: false,
            staleTime: 1000 * 60 * 60 // 1시간
        }
    }
});
function App() {
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    var _a = react_1.useState(2), bookId = _a[0], setBookId = _a[1];
    var drillingProps = {
        bookId: bookId,
        queryClient: exports.queryClient
    };
    return (React.createElement(react_query_1.QueryClientProvider, { client: exports.queryClient },
        React.createElement(BookEntityDraggableContext_1["default"], __assign({}, drillingProps))));
}
exports["default"] = App;
