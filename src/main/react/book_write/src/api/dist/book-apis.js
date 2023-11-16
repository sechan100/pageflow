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
exports.__esModule = true;
exports.useUpdateBook = exports.useRearrangeOutlineMutation = exports.useGetOutline = void 0;
/* eslint-disable @typescript-eslint/no-unused-vars */
var react_query_1 = require("react-query");
var axios_1 = require("axios");
var react_query_2 = require("react-query");
var App_1 = require("../App");
var flowAlert_1 = require("../etc/flowAlert");
// 더미 데이터
var sampleOutline = {
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
    coverImgUrl: "https://phinf.pstatic.net/contact/20230727_252/1690456995185MmBBn_JPEG/image.jpg",
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
// useGetOutline 내부적으로 호출하는 axios api
var getOutlineById = function (id) { return __awaiter(void 0, void 0, Promise, function () {
    var response;
    return __generator(this, function (_a) {
        switch (_a.label) {
            case 0: return [4 /*yield*/, axios_1["default"].get("/api/book/" + id + "/outline")];
            case 1:
                response = _a.sent();
                if (response.status !== 200) {
                    throw new Error("책 정보를 가져오는데 실패했습니다.");
                }
                if (response.data) {
                    console.log("Outline Get Success!");
                    console.log(response.data);
                }
                return [2 /*return*/, response.data];
        }
    });
}); };
// 목차정보 가져오는 api 훅
exports.useGetOutline = function (bookId) {
    var _a = react_query_1.useQuery(['book', bookId], // query key
    function () { return getOutlineById(bookId); }, // query fn
    {
        enabled: bookId > 0
    }), data = _a.data, isLoading = _a.isLoading, isFetching = _a.isFetching, error = _a.error;
    // 로딩중이거나 페칭중이라면 sampleOutline을 반환
    if (isLoading || isFetching) {
        return sampleOutline;
    }
    if (data) {
        var outline = data;
        return outline;
    }
    else {
        return sampleOutline;
    }
};
// 목차정보 업데이트 api 훅
exports.useRearrangeOutlineMutation = function (bookId) {
    var _a = react_query_2.useMutation(function (newOutline) { return axios_1["default"].put("/api/book/" + bookId + "/outline", newOutline); }, {
        onSuccess: function () {
            App_1.queryClient.invalidateQueries(['book', bookId]);
        }
    }), mutateAsync = _a.mutateAsync, isLoading = _a.isLoading, error = _a.error;
    return {
        mutateAsync: mutateAsync,
        isLoading: isLoading,
        error: error
    };
};
exports.useUpdateBook = function (bookId) {
    var _a = react_query_2.useMutation(function (bookUpdateRequest) { return __awaiter(void 0, void 0, void 0, function () {
        var formDate, response;
        return __generator(this, function (_a) {
            switch (_a.label) {
                case 0:
                    formDate = new FormData();
                    formDate.append("title", bookUpdateRequest.title);
                    if (bookUpdateRequest.coverImg)
                        formDate.append("coverImg", bookUpdateRequest.coverImg);
                    return [4 /*yield*/, axios_1["default"].put("/api/book/" + bookId, formDate, {
                            headers: {
                                'Content-Type': 'multipart/form-data'
                            }
                        })];
                case 1:
                    response = _a.sent();
                    if (response.status !== 200) {
                        flowAlert_1["default"]("error", "책 정보를 업데이트하는데 실패했습니다.");
                        throw new Error("책 정보를 업데이트하는데 실패했습니다.");
                    }
                    if (response.data) {
                        console.log("Book Update Success!");
                        console.log(response.data);
                        return [2 /*return*/, response.data];
                    }
                    return [2 /*return*/];
            }
        });
    }); }, {
        onSuccess: function (data) {
            var staleBook = App_1.queryClient.getQueryData(['book', bookId]);
            // 클라이언트 캐시 데이터 업데이트: 서버와 재통신 하지 않고, 그냥 캐시만 낙관적으로 업데이트한다.
            if (staleBook) {
                App_1.queryClient.setQueryData(['book', bookId], __assign(__assign({}, staleBook), { title: data.title, coverImgUrl: data.coverImgUrl }));
            }
        }
    }), mutateAsync = _a.mutateAsync, isLoading = _a.isLoading, error = _a.error;
    return {
        mutateAsync: mutateAsync,
        isLoading: isLoading,
        error: error
    };
};
