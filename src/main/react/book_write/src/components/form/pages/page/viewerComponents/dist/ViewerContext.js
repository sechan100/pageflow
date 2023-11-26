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
exports.useNavStore = void 0;
var zustand_1 = require("zustand");
var page_api_1 = require("../api/page-api");
var PageCursor_1 = require("../nav/PageCursor");
var Navbar_1 = require("../nav/Navbar");
var react_1 = require("react");
exports.useNavStore = zustand_1.create(function (set, get) { return ({
    isVisible: false,
    toggle: function () {
        var isVisible = get().isVisible;
        set({ isVisible: !isVisible });
    }
}); });
function ViewerContext(_a) {
    var outline = _a.outline;
    var toggle = exports.useNavStore().toggle;
    var _b = PageCursor_1.useLocationStore(), location = _b.location, metaPage = _b.metaPage;
    var getPageAsync = page_api_1.useGetPage(outline.id, getPageMap(outline));
    var currentPage = getPageAsync(location);
    var contentContainer = react_1.useRef(null);
    react_1.useEffect(function () {
        if (contentContainer.current) {
            var outedIdx = findOverflowIndexIncludingImages(contentContainer.current);
            var text = currentPage.content;
            console.log(outedIdx);
            console.log(text.substring(outedIdx.charIndex - 50, outedIdx.charIndex));
        }
    }, [currentPage]);
    return (React.createElement("div", { className: "", onClick: toggle },
        React.createElement("div", { className: "text-center py-20 sm:px-10 xl:px-52" },
            metaPage.isMetaPage && metaPage.type === PageCursor_1.metaPageType.CHAPTER_INIT &&
                React.createElement("div", { className: "text-2xl mt-20" }, Navbar_1.getChapterTitle(outline, location.chapterIdx)),
            React.createElement("div", { className: "text-justify" }, !metaPage.isMetaPage &&
                React.createElement("div", { id: "viewer-page-content-container", ref: contentContainer, style: { columnFill: "auto", columnGap: "8%" }, className: "select-none columns-2 h-[79vh] overflow-hidden", dangerouslySetInnerHTML: { __html: currentPage.content } })))));
    // [chapterIdx, PageIdx] 구조인 ILocation 타입을 key, pageId를 value로 하는 map을 반환한다.
    function getPageMap(outline) {
        var pageMap = new Map();
        outline.chapters.forEach(function (chapter, chapterIdx) {
            var _a;
            (_a = chapter.pages) === null || _a === void 0 ? void 0 : _a.forEach(function (page, pageIdx) {
                pageMap.set(chapterIdx + "," + pageIdx, page.id);
            });
        });
        return pageMap;
    }
    function findOverflowIndexIncludingImages(element) {
        var nodes = getNodes(element);
        var elementRect = getDoubleHeightRect(element);
        var currentHeight = 0;
        for (var i = 0; i < nodes.length; i++) {
            var node = nodes[i];
            if (node.nodeType === Node.TEXT_NODE) {
                var range = document.createRange();
                range.selectNodeContents(node);
                if (node.textContent === null)
                    continue;
                for (var j = 0; j < node.textContent.length; j++) {
                    range.setEnd(node, j + 1);
                    var rangeRect = range.getBoundingClientRect();
                    // 현재 문자까지의 텍스트 높이를 누적 높이에 추가
                    var tempHeight = currentHeight + (rangeRect.bottom - rangeRect.top);
                    if (tempHeight > elementRect.bottom) {
                        return { nodeIndex: i, charIndex: j }; // 넘어가는 지점의 인덱스 반환
                    }
                }
                // 텍스트 노드의 전체 높이를 누적
                currentHeight += range.getBoundingClientRect().height;
            }
            else if (node.nodeType === Node.ELEMENT_NODE && node.tagName === 'IMG') {
                var imgRect = node.getBoundingClientRect();
                var tempHeight = currentHeight + imgRect.height;
                if (tempHeight > elementRect.bottom) {
                    return { nodeIndex: i, charIndex: -1 }; // 이미지가 넘어가는 지점
                }
                currentHeight += imgRect.height; // 이미지 높이를 누적
            }
        }
        return { nodeIndex: -1, charIndex: -1 }; // 모든 콘텐츠가 요소 내에 있음
    }
    function getNodes(element) {
        var nodes = [];
        var treeWalker = document.createTreeWalker(element, NodeFilter.SHOW_ALL);
        while (treeWalker.nextNode()) {
            nodes.push(treeWalker.currentNode);
        }
        return nodes;
    }
    function getDoubleHeightRect(element) {
        var rect = element.getBoundingClientRect();
        return __assign(__assign({}, rect), { height: rect.height * 2 });
    }
}
exports["default"] = ViewerContext;
