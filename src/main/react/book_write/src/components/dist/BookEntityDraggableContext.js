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
var react_beautiful_dnd_1 = require("react-beautiful-dnd");
var BookBasicPageForm_1 = require("./form/BookBasicPageForm");
var OutlineSidebar_1 = require("./outline/OutlineSidebar");
var book_apis_1 = require("../api/book-apis");
var react_1 = require("react");
var Chapter_1 = require("./outline/Chapter");
function BookEntityDraggableContext(drillingProps) {
    var bookId = drillingProps.bookId, queryClient = drillingProps.queryClient;
    // react query로 server book outline snapshot을 가져온다.
    var outline = book_apis_1.useGetOutline(bookId);
    var deleteDropArea = react_1.useRef(null); // 삭제 드롭 영역의 DOM 참조
    var turnOnTargetDraggingState = function (start) {
        // 삭제 드롭영역 보이기
        if (deleteDropArea.current) {
            // @ts-ignore
            deleteDropArea.current.classList.toggle("visible");
            // @ts-ignore
            deleteDropArea.current.classList.toggle("invisible");
        }
    };
    // 첫 매개변수로 받은 배열의 sourceIndex와 destinationIndex의 요소를 서로 교환하여 반환한다.
    function getReorderedArray(array, sourceIndex, destinationIndex) {
        var newArray = Array.from(array);
        var removedElement = newArray.splice(sourceIndex, 1)[0]; // 기존 위치의 요소를 제거
        newArray.splice(destinationIndex, 0, removedElement); // 새로운 위치에 요소를 삽입
        return newArray;
    }
    // DroppableId로부터 sourceChapter와 destinationChapter를 가져온다; destinationChapter가 닫힌 상태의 챕터라면, DrppableId가 다르기 때문에, 닫힌 챕터에 드롭되었는지의 여부도 인자로 받는다.
    function getSourceChapterAndDestinationChapter(sourceChapterDroppableId, destinationChapterDroppableId, isInClosingPageDropAreaDropped) {
        if (!outline.chapters)
            return [null, null];
        var sourceChapter = outline.chapters.find(function (chapter) { return OutlineSidebar_1.pageDropAreaPrefix + chapter.id === sourceChapterDroppableId; }); // 드래그 된 페이지가 원래 속한 챕터
        var destinationChapter;
        // 닫혀있는 챕터로 destinationChapter를 찾음
        if (isInClosingPageDropAreaDropped) {
            destinationChapter = outline.chapters.find(function (chapter) { return Chapter_1.inClosingPageDropAreaPrefix + String(chapter.id) === destinationChapterDroppableId; });
            // 열린 상태인 챕터로 destinationChapter를 찾음
        }
        else {
            destinationChapter = outline.chapters.find(function (chapter) { return OutlineSidebar_1.pageDropAreaPrefix + String(chapter.id) === destinationChapterDroppableId; });
        }
        if (sourceChapter && destinationChapter) {
            return [sourceChapter, destinationChapter];
        }
        else {
            console.log('sourceChapter, 또는 destinationChapter가 정의되지 않았습니다.');
            return [null, null];
        }
    }
    var onDragEnd = function (result) {
        console.log(result.destination);
        // 삭제 드롭영역 숨기기
        if (deleteDropArea.current) {
            // @ts-ignore
            deleteDropArea.current.classList.toggle("visible");
            // @ts-ignore
            deleteDropArea.current.classList.toggle("invisible");
        }
        var destination = result.destination, // 최종 드롭된 위치(목적지)
        source = result.source, // 기존 위치(출발지)
        type = result.type // 드래그된 요소의 타입(특정 타입이 적용된 Droppable의 직계 자식은 해당 타입을 가진다.)
        ;
        if (!destination) {
            return;
        }
        // 원래 위치와 동일한 위치로 드래그 되었을 경우 state를 유지.
        if (destination.droppableId === source.droppableId && destination.index === source.index) {
            return;
        }
        // re-order: 1. 챕터간의 순서 변경
        if (type === 'CHAPTER' && outline.chapters) {
            var newChapters = getReorderedArray(outline.chapters, source.index, destination.index);
            setStateChapters(newChapters);
            return;
            // 페이지 변경
        }
        else if (type === 'PAGE' && outline.chapters) {
            // 닫힌 상태의 챕터에 드롭되었는지 여부
            var isInClosingPageDropAreaDropped = destination.droppableId.startsWith(Chapter_1.inClosingPageDropAreaPrefix);
            // 출발지와 목적지의 챕터를 가져옴
            var _a = getSourceChapterAndDestinationChapter(source.droppableId, destination.droppableId, isInClosingPageDropAreaDropped), sourceChapter = _a[0], destinationChapter = _a[1];
            // 올바른 드롭 이벤트가 아닌 경우 종료.
            if (!sourceChapter || !destinationChapter)
                return;
            if (!sourceChapter.pages || !destinationChapter.pages) {
                return;
            }
            // re-order: 2. 페이지의 챕터 내부 순서 변경 (출발지와 목적지가 같은 경우)
            if (sourceChapter === destinationChapter) {
                var newPages = getReorderedArray(sourceChapter.pages, source.index, destination.index);
                setStatePages(newPages, sourceChapter.id);
                // re-order: 3. 페이지의 챕터간 이동
            }
            else if (sourceChapter !== destinationChapter) {
                var newSourcePages = Array.from(sourceChapter.pages);
                var newDestinationPages = Array.from(destinationChapter.pages);
                var removedPage = newSourcePages.splice(source.index, 1)[0];
                // re-order: 3-1. 닫혀있는 상태의 챕터로 드롭된 경우 -> 해당 챕터의 제일 마지막 인덱스로 추가.
                if (isInClosingPageDropAreaDropped) {
                    newDestinationPages.splice(destinationChapter.pages.length, 0, removedPage);
                    // re-order: 3-2. 챕터가 열려있는 상태에서 순서까지 같이 지정하여 드롭한 경우. -> 지정된 index 위치로 추가.
                }
                else {
                    newDestinationPages.splice(destination.index, 0, removedPage);
                }
                var newSourceChapter_1 = __assign(__assign({}, sourceChapter), { pages: newSourcePages });
                var newDestinationChapter_1 = __assign(__assign({}, destinationChapter), { pages: newDestinationPages });
                var newChapters = outline.chapters.map(function (chapter) {
                    // sourceChapter인 경우, 새로운 newSourceChapter로 교체
                    if (chapter.id === newSourceChapter_1.id) {
                        return newSourceChapter_1;
                        // destinationChapter 경우, 새로운 newDestinationChapter 교체
                    }
                    else if (chapter.id === newDestinationChapter_1.id) {
                        return newDestinationChapter_1;
                        // 나머지는 그대로 유지
                    }
                    else {
                        return chapter;
                    }
                });
                setStateChapters(newChapters);
            }
        }
    };
    // queryClient의 ["book", bookId]로 저장된 서버 스냅샷을 업데이트한다. 
    function setStateChapters(newChapters) {
        var newOutline = __assign(__assign({}, outline), { chapters: newChapters });
        queryClient.setQueryData(["book", bookId], newOutline);
    }
    // queryClient의 ["book", bookId]로 저장된 서버 스냅샷을 업데이트한다. 
    function setStatePages(pages, sourceChapterId) {
        if (!outline.chapters)
            return;
        var newChapters = outline.chapters.map(function (chapter) {
            // 변경된 페이지를 가지는 챕터는 새로운 페이지로 교체
            if (chapter.id === sourceChapterId) {
                return __assign(__assign({}, chapter), { pages: pages });
                // 나머지 페이지는 유지
            }
            else {
                return chapter;
            }
        });
        setStateChapters(newChapters);
    }
    return (React.createElement(React.Fragment, null,
        React.createElement(react_beautiful_dnd_1.DragDropContext, { onDragStart: turnOnTargetDraggingState, onDragEnd: onDragEnd },
            React.createElement(OutlineSidebar_1["default"], __assign({}, drillingProps)),
            React.createElement(react_beautiful_dnd_1.Droppable, { droppableId: "delete-drop-area", type: 'CHAPTER' }, function (provided, snapshot) { return (React.createElement("div", { className: "bg-gray-500 animate-bounce hover:bg-gray-700 w-48 absolute invisible left-1/2 top-5 p-5 px-6 rounded-full", ref: deleteDropArea },
                React.createElement("div", __assign({ ref: provided.innerRef }, provided.droppableProps, { className: "flex" }),
                    React.createElement("svg", { className: "w-6 h-6 text-gray-800 dark:text-white mr-3", "aria-hidden": "true", xmlns: "http://www.w3.org/2000/svg", fill: "none", viewBox: "0 0 18 20" },
                        React.createElement("path", { stroke: "currentColor", "stroke-linecap": "round", "stroke-linejoin": "round", "stroke-width": "2", d: "M1 5h16M7 8v8m4-8v8M7 1h4a1 1 0 0 1 1 1v3H6V2a1 1 0 0 1 1-1ZM3 5h12v13a1 1 0 0 1-1 1H4a1 1 0 0 1-1-1V5Z" })),
                    React.createElement("span", { className: "text-white" }, "\uB4DC\uB798\uADF8\uD558\uC5EC \uC0AD\uC81C")))); }),
            React.createElement("main", { className: "px-24 mt-16 flex-auto" },
                React.createElement(BookBasicPageForm_1["default"], __assign({}, drillingProps))))));
}
exports["default"] = BookEntityDraggableContext;
