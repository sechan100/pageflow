"use strict";
exports.__esModule = true;
exports.QueryContext = void 0;
require("./App.css");
var react_query_1 = require("react-query");
var react_1 = require("react");
var BookEntityDraggableContext_1 = require("./components/BookEntityDraggableContext");
require("react-image-crop/dist/ReactCrop.css");
var react_2 = require("react");
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
exports.QueryContext = react_2["default"].createContext({ queryClient: queryClient, bookId: 0 });
function App() {
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    var _a = react_1.useState(2), bookId = _a[0], setBookId = _a[1];
    return (react_2["default"].createElement(react_query_1.QueryClientProvider, { client: queryClient },
        react_2["default"].createElement(exports.QueryContext.Provider, { value: { queryClient: queryClient, bookId: bookId } },
            react_2["default"].createElement(BookEntityDraggableContext_1["default"], null))));
}
exports["default"] = App;
