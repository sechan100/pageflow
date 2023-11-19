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
var __awaiter = (this && this.__awaiter) || function (thisArg, _arguments, P, generator) {
    function adopt(value) { return value instanceof P ? value : new P(function (resolve) { resolve(value); }); }
    return new (P || (P = Promise))(function (resolve, reject) {
        function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
        function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
        function step(result) { result.done ? resolve(result.value) : adopt(result.value).then(fulfilled, rejected); }
        step((generator = generator.apply(thisArg, _arguments || [])).next());
    });
};
var __generator = (this && this.__generator) || function (thisArg, body) {
    var _ = { label: 0, sent: function() { if (t[0] & 1) throw t[1]; return t[1]; }, trys: [], ops: [] }, f, y, t, g;
    return g = { next: verb(0), "throw": verb(1), "return": verb(2) }, typeof Symbol === "function" && (g[Symbol.iterator] = function() { return this; }), g;
    function verb(n) { return function (v) { return step([n, v]); }; }
    function step(op) {
        if (f) throw new TypeError("Generator is already executing.");
        while (_) try {
            if (f = 1, y && (t = op[0] & 2 ? y["return"] : op[0] ? y["throw"] || ((t = y["return"]) && t.call(y), 0) : y.next) && !(t = t.call(y, op[1])).done) return t;
            if (y = 0, t) op = [op[0] & 2, t.value];
            switch (op[0]) {
                case 0: case 1: t = op; break;
                case 4: _.label++; return { value: op[1], done: false };
                case 5: _.label++; y = op[1]; op = [0]; continue;
                case 7: op = _.ops.pop(); _.trys.pop(); continue;
                default:
                    if (!(t = _.trys, t = t.length > 0 && t[t.length - 1]) && (op[0] === 6 || op[0] === 2)) { _ = 0; continue; }
                    if (op[0] === 3 && (!t || (op[1] > t[0] && op[1] < t[3]))) { _.label = op[1]; break; }
                    if (op[0] === 6 && _.label < t[1]) { _.label = t[1]; t = op; break; }
                    if (t && _.label < t[2]) { _.label = t[2]; _.ops.push(op); break; }
                    if (t[2]) _.ops.pop();
                    _.trys.pop(); continue;
            }
            op = body.call(thisArg, _);
        } catch (e) { op = [6, e]; y = 0; } finally { f = t = 0; }
        if (op[0] & 5) throw op[1]; return { value: op[0] ? op[1] : void 0, done: true };
    }
};
var __spreadArrays = (this && this.__spreadArrays) || function () {
    for (var s = 0, i = 0, il = arguments.length; i < il; i++) s += arguments[i].length;
    for (var r = Array(s), k = 0, i = 0; i < il; i++)
        for (var a = arguments[i], j = 0, jl = a.length; j < jl; j++, k++)
            r[k] = a[j];
    return r;
};
exports.__esModule = true;
/* eslint-disable @typescript-eslint/no-unused-vars */
var axios_1 = require("axios");
var react_1 = require("react");
var App_1 = require("../../App");
function OutlineSidebarWrapper(props) {
    return (React.createElement("div", { className: 'flex' },
        React.createElement("aside", { id: "page-outline-sidebar", className: "fixed z-10 top-0 left-0 w-64 h-screen transition-transform -translate-x-full sm:translate-x-0" },
            React.createElement("div", { className: "overflow-y-auto pt-12 pb-5 px-3 h-full bg-white border-r border-gray-200 dark:bg-gray-800 dark:border-gray-700" }, props.children),
            React.createElement(AddChapterBtn, null)),
        React.createElement("div", { id: "sidebar-placeholder", className: "w-64 h-screen hidden sm:block" })));
}
exports["default"] = OutlineSidebarWrapper;
function AddChapterBtn() {
    var _a = react_1.useContext(App_1.QueryContext), bookId = _a.bookId, queryClient = _a.queryClient;
    // 서버에서 새로운 Chapter 생성요청을 전달하고 받아온 생성된 Chapter를 react query가 관리하는 캐쉬에 반영.
    function addChapter() {
        return __awaiter(this, void 0, void 0, function () {
            var queryCache, response, newChapter, newChapterSummary;
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0:
                        queryCache = queryClient.getQueryData(['book', bookId]);
                        return [4 /*yield*/, axios_1["default"].post("/api/book/" + bookId + "/chapter")];
                    case 1:
                        response = _a.sent();
                        if (response.status !== 200) {
                            throw new Error("새로운 챕터를 생성하지 못했습니다.");
                        }
                        newChapter = response.data;
                        newChapterSummary = {
                            id: newChapter.id,
                            title: newChapter.title,
                            sortPriority: newChapter.sortPriority,
                            pages: newChapter.pages
                        };
                        queryClient.setQueryData(['book', bookId], function (oldData) {
                            var staleOutline = oldData;
                            // 기존 챕터가 없던경우 새로운 챕터만 추가해서 반환
                            if (!staleOutline.chapters) {
                                return __assign(__assign({}, staleOutline), { chapters: [
                                        newChapterSummary
                                    ] });
                            }
                            else {
                                // 기존 챕터가 있던 경우 원래 있던거에 추가해서 반환
                                return __assign(__assign({}, staleOutline), { chapters: __spreadArrays(staleOutline.chapters, [
                                        newChapterSummary
                                    ]) });
                            }
                        });
                        return [2 /*return*/];
                }
            });
        });
    }
    return (React.createElement("button", { type: "button", onClick: addChapter, className: "fixed z-20 top-4 left-40 px-5 py-2 text-xs font-medium text-center ext-gray-900 focus:outline-none bg-white rounded-lg border border-gray-200 hover:bg-gray-100 hover:text-blue-700 dark:focus:ring-gray-700 dark:bg-gray-800 dark:text-gray-400 dark:border-gray-600 dark:hover:text-white dark:hover:bg-gray-700" }, "\uC0C8 \uCC55\uD130"));
}
