"use strict";
/* eslint-disable react-hooks/exhaustive-deps */
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
var __spreadArrays = (this && this.__spreadArrays) || function () {
    for (var s = 0, i = 0, il = arguments.length; i < il; i++) s += arguments[i].length;
    for (var r = Array(s), k = 0, i = 0; i < il; i++)
        for (var a = arguments[i], j = 0, jl = a.length; j < jl; j++, k++)
            r[k] = a[j];
    return r;
};
exports.__esModule = true;
exports.usePageMutationStore = void 0;
var react_1 = require("react");
var zustand_1 = require("zustand");
var react_router_dom_1 = require("react-router-dom");
var page_api_1 = require("../../../../api/page-api");
var QuillEditor_1 = require("./QuillEditor");
exports.usePageMutationStore = zustand_1.create(function (set) { return ({
    payload: [],
    isMutated: false,
    resetMutation: function () { return set(function (state) { return (__assign(__assign({}, state), { payload: [], isMutated: false })); }); },
    isLoading: false,
    dispatchs: {
        updatePage: function (_a) {
            var id = _a.id, title = _a.title, content = _a.content;
            set(function (state) {
                // 해당 PageId를 가진 변경사항이 존재하는 경우, 해당 부분을 수정, 
                // 존재하지 않는 경우, 새로운 PageMutation을 추가한다.
                var isExist = state.payload.find(function (page) { return page.id === id; });
                // 기존 변경사항이 존재하는 경우
                if (isExist) {
                    return __assign(__assign({}, state), { payload: state.payload.map(function (page) {
                            if (page.id === id)
                                return __assign(__assign({}, page), { title: title, content: content });
                            else
                                return page;
                        }), isMutated: true });
                    // 기존 변경사항이 존재하지 않는 경우  
                }
                else {
                    return __assign(__assign({}, state), { payload: __spreadArrays(state.payload, [
                            {
                                id: id,
                                title: title,
                                content: content
                            }
                        ]), isMutated: true });
                }
            });
        }
    }
}); });
function PageForm() {
    var _a = react_router_dom_1.useParams(), chapterId = _a.chapterId, pageId = _a.pageId;
    var page = page_api_1.useGetPageQuery(Number(pageId));
    var pageStore = exports.usePageMutationStore();
    var _b = react_1.useState(page), localPage = _b[0], setLocalPage = _b[1];
    // outline 데이터의 변경시, 이미 선언된 state인 ChapterMutation의 상태를 업데이트하기 위함
    react_1.useEffect(function () {
        if (page) {
            setLocalPage({
                id: page.id,
                title: page.title,
                content: page.content
            });
        }
    }, [page]);
    // 로컬 데이터의 변경사항을 zustand store에 업데이트
    react_1.useEffect(function () {
        // local의 title 데이터가 존재하면서 outline의 title 데이터와 다를 경우 => title 변경사항이 존재
        if (localPage.title && isTitleChanged(localPage.title))
            pageStore.dispatchs.updatePage({
                id: localPage.id,
                title: localPage.title,
                content: localPage.content
            });
    }, [localPage]);
    return (React.createElement(React.Fragment, null,
        React.createElement("div", { className: "px-24 mt-16" },
            React.createElement("div", { className: "sm:col-span-2" },
                React.createElement("label", { htmlFor: "title", className: "block mb-2 text-md font-medium text-gray-900" }, "\uD398\uC774\uC9C0 \uC81C\uBAA9"),
                React.createElement("input", { value: localPage.title ? localPage.title : '', onChange: handleTitleChange, type: "text", name: "title", id: "title", className: "bg-gray-50 border border-gray-300 text-gray-900 text-xl rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5", placeholder: "\uCC45 \uC81C\uBAA9\uC744 \uC785\uB825\uD574\uC8FC\uC138\uC694." }))),
        React.createElement("br", null),
        React.createElement("br", null),
        React.createElement(QuillEditor_1["default"], null)));
    // 현재 서버 상태에 저장된 데이터와 다른지...
    function isTitleChanged(title) {
        return (page === null || page === void 0 ? void 0 : page.title) !== title;
    }
    function handleTitleChange(e) {
        setLocalPage(function (state) {
            return __assign(__assign({}, state), { title: e.target.value });
        });
    }
}
exports["default"] = PageForm;
