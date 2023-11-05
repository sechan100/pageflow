"use strict";
exports.__esModule = true;
require("./App.css");
var BookBasicPageForm_1 = require("./components/form/BookBasicPageForm");
var react_query_1 = require("react-query");
var OutlineSidebar_1 = require("./components/outline/OutlineSidebar");
var queryClient = new react_query_1.QueryClient();
function App() {
    return (React.createElement(react_query_1.QueryClientProvider, { client: queryClient },
        React.createElement(OutlineSidebar_1["default"], null),
        React.createElement("main", { className: "px-24 mt-16 flex-auto" },
            React.createElement(BookBasicPageForm_1["default"], null))));
}
exports["default"] = App;
