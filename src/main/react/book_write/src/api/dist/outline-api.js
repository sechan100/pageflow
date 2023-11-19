"use strict";
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
exports.__esModule = true;
exports.useOutlineMutation = exports.useGetOutlineQuery = void 0;
var axios_1 = require("axios");
var react_query_1 = require("react-query");
var App_1 = require("../App");
var react_1 = require("react");
var fallback = {
    id: 0,
    author: {
        id: 0,
        createDate: "생성일",
        modifyDate: "수정일",
        nickname: "닉네임",
        profileImgUrl: "https://phinf.pstatic.net/contact/20230727_252/1690456995185MmBBn_JPEG/image.jpg"
    },
    title: "책이 로딩중입니다...",
    published: false,
    coverImgUrl: "/img/unloaded_img.jpg",
    chapters: [
        {
            id: 0,
            title: "챕터가 로딩중입니다...",
            sortPriority: 10000,
            pages: [
                {
                    id: 0,
                    title: "페이지가 로딩중입니다...",
                    sortPriority: 10000
                }
            ]
        }
    ]
};
// 목차정보 가져오는 api 훅
exports.useGetOutlineQuery = function (bookId) {
    var _a = react_query_1.useQuery(['book', bookId], // query key
    function () { return getOutlineById(bookId); }, // query fn
    {
        enabled: bookId > 0
    }), data = _a.data, isLoading = _a.isLoading, isFetching = _a.isFetching;
    if (data && !isLoading && !isFetching) {
        return data;
    }
    else {
        return fallback;
    }
};
// useGetOutline 내부적으로 호출하는 axios api
var getOutlineById = function (id) { return __awaiter(void 0, void 0, void 0, function () {
    var response;
    return __generator(this, function (_a) {
        switch (_a.label) {
            case 0: return [4 /*yield*/, axios_1["default"].get("/api/book/" + id + "/outline")];
            case 1:
                response = _a.sent();
                if (response && response.status === 200 && response.data) {
                    console.log("====[ Success to Fetching Outline ]====", response.data);
                    return [2 /*return*/, response.data];
                }
                else {
                    console.log("Outline 데이터를 가져오는데 실패했습니다.");
                    throw new Error("목차 정보를 가져오는데 실패했습니다.");
                }
                return [2 /*return*/];
        }
    });
}); };
// 목차정보 업데이트 api 훅
exports.useOutlineMutation = function (bookId) {
    var queryClient = react_1.useContext(App_1.QueryContext).queryClient;
    var _a = react_query_1.useMutation(function (outlineUpdateBody) { return axios_1["default"].put("/api/book/" + bookId + "/outline", outlineUpdateBody); }, {
        onSuccess: function () {
            queryClient.invalidateQueries(['book', bookId]);
        }
    }), mutateAsync = _a.mutateAsync, isLoading = _a.isLoading, isError = _a.isError;
    return { mutateAsync: mutateAsync, isLoading: isLoading, isError: isError };
};
