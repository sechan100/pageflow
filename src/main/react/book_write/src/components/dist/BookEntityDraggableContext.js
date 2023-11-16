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
var react_beautiful_dnd_1 = require("react-beautiful-dnd");
var OutlineSidebar_1 = require("./outline/OutlineSidebar");
var book_apis_1 = require("../api/book-apis");
var react_1 = require("react");
var Chapter_1 = require("./outline/Chapter");
var axios_1 = require("axios");
var flowAlert_1 = require("../etc/flowAlert");
var FormMain_1 = require("./form/FormMain");
function BookEntityDraggableContext(props) {
    var _this = this;
    var bookId = props.bookId, queryClient = props.queryClient;
    // react query로 server book outline snapshot을 가져온다.
    var outline = book_apis_1.useGetOutline(bookId);
    var chapterDeleteDropArea = react_1.useRef(null); // Chapter 삭제 드롭 영역의 DOM 참조
    var pageDeleteDropArea = react_1.useRef(null); // Page 삭제 드롭 영역의 DOM 참조
    // outline 재정렬 여부에 대한 상태를 기록하고, 변경된 데이터 버퍼를 전송할지 말지에 관한 상태를 나타낸다.
    var _a = react_1.useReducer(function (status, action) {
        switch (action.type) {
            case 'flushed':
                return "flushed";
            case 'mutated':
                return "mutated";
            case 'waiting':
                return "waiting";
            default:
                throw new Error();
        }
    }, "flushed"), outlineBufferStatus = _a[0], outlineBufferStatusDispatch = _a[1];
    var _b = book_apis_1.useRearrangeOutlineMutation(bookId), mutateAsync = _b.mutateAsync, isLoading = _b.isLoading, error = _b.error;
    // 서버에 Outline 데이터의 재정렬 업데이트 요청을 보내는 함수
    function updateOutlineOnServer(outline) {
        return __awaiter(this, void 0, void 0, function () {
            var error_1;
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0:
                        if (!(outlineBufferStatus === 'mutated' || outlineBufferStatus === 'waiting')) return [3 /*break*/, 4];
                        _a.label = 1;
                    case 1:
                        _a.trys.push([1, 3, , 4]);
                        return [4 /*yield*/, mutateAsync(outline)];
                    case 2:
                        _a.sent();
                        flowAlert_1["default"]('success', "목차 정보가 저장되었습니다.");
                        // 요청을 전달한 후에 성공적으로 업데이트 되었다면, outlineBufferStatus를 flushed로 변경한다.
                        outlineBufferStatusDispatch({ type: 'flushed' });
                        return [3 /*break*/, 4];
                    case 3:
                        error_1 = _a.sent();
                        flowAlert_1["default"]('error', "목차 정보를 서버와 동기화하지 못했습니다.");
                        return [3 /*break*/, 4];
                    case 4: return [2 /*return*/];
                }
            });
        });
    }
    // outlineBuffer가 변경될 때마다 5초 뒤 서버에 재정렬 요청을 보내는 타이머를 시작, 도중에 outlineBuffer가 변경되면 타이머를 초기화한다.
    react_1.useEffect(function () {
        // outlineBufferStatus가 mutated인 경우, 업데이트 요청을 전송하기위한 타이머를 시작한다.
        if (outlineBufferStatus === 'mutated') {
            var outlineBufferFlushTimer_1 = setTimeout(function () { return __awaiter(_this, void 0, void 0, function () {
                return __generator(this, function (_a) {
                    updateOutlineOnServer(outline);
                    return [2 /*return*/];
                });
            }); }, 7000);
            return function () {
                clearTimeout(outlineBufferFlushTimer_1);
            };
        }
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [outlineBufferStatus]);
    return (React.createElement(React.Fragment, null,
        React.createElement(react_beautiful_dnd_1.DragDropContext, { onDragStart: onDragStart, onDragEnd: onDragEnd },
            React.createElement(OutlineSidebar_1["default"], __assign({}, props, { outlineBufferStatusReducer: [outlineBufferStatus, outlineBufferStatusDispatch] })),
            React.createElement(react_beautiful_dnd_1.Droppable, { droppableId: "chapter-delete-drop-area", type: "CHAPTER" }, function (provided, snapshot) { return (React.createElement("div", { className: "bg-gray-500 animate-bounce hover:bg-gray-700 w-48 absolute invisible left-1/2 top-5 p-5 px-6 rounded-full", ref: chapterDeleteDropArea },
                React.createElement("div", __assign({ ref: provided.innerRef }, provided.droppableProps, { className: "flex" }),
                    React.createElement("svg", { className: "w-6 h-6 text-gray-800 dark:text-white mr-3", "aria-hidden": "true", xmlns: "http://www.w3.org/2000/svg", fill: "none", viewBox: "0 0 18 20" },
                        React.createElement("path", { stroke: "currentColor", "stroke-linecap": "round", "stroke-linejoin": "round", "stroke-width": "2", d: "M1 5h16M7 8v8m4-8v8M7 1h4a1 1 0 0 1 1 1v3H6V2a1 1 0 0 1 1-1ZM3 5h12v13a1 1 0 0 1-1 1H4a1 1 0 0 1-1-1V5Z" })),
                    React.createElement("span", { className: "text-white" }, "\uB4DC\uB798\uADF8\uD558\uC5EC \uC0AD\uC81C")))); }),
            React.createElement(react_beautiful_dnd_1.Droppable, { droppableId: "page-delete-drop-area", type: "PAGE" }, function (provided, snapshot) { return (React.createElement("div", { className: "bg-gray-500 animate-bounce hover:bg-gray-700 w-48 absolute invisible left-1/2 top-5 p-5 px-6 rounded-full", ref: pageDeleteDropArea },
                React.createElement("div", __assign({ ref: provided.innerRef }, provided.droppableProps, { className: "flex" }),
                    React.createElement("svg", { className: "w-6 h-6 text-gray-800 dark:text-white mr-3", "aria-hidden": "true", xmlns: "http://www.w3.org/2000/svg", fill: "none", viewBox: "0 0 18 20" },
                        React.createElement("path", { stroke: "currentColor", "stroke-linecap": "round", "stroke-linejoin": "round", "stroke-width": "2", d: "M1 5h16M7 8v8m4-8v8M7 1h4a1 1 0 0 1 1 1v3H6V2a1 1 0 0 1 1-1ZM3 5h12v13a1 1 0 0 1-1 1H4a1 1 0 0 1-1-1V5Z" })),
                    React.createElement("span", { className: "text-white" }, "\uB4DC\uB798\uADF8\uD558\uC5EC \uC0AD\uC81C")))); }),
            React.createElement(FormMain_1["default"], __assign({}, props)))));
    function onDragStart(start) {
        toggleDeleteDropAreaVisibility(start.type);
        // 만약 이전에 mutated였다면, 버퍼 전송을 flush하지는 않지만 잠시 멈춰두기 위해서 waiting으로 변경한다.
        if (outlineBufferStatus === 'mutated') {
            outlineBufferStatusDispatch({ type: 'waiting' });
        }
    }
    ;
    function onDragEnd(result) {
        toggleDeleteDropAreaVisibility(result.type);
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
        // 삭제 영역으로 드롭된 경우 삭제 로직 실행
        if (destination.droppableId === 'chapter-delete-drop-area' || destination.droppableId === 'page-delete-drop-area') {
            deleteDroppedElement(type, source, destination);
            return;
        }
        // re-order: 1. 챕터간의 순서 변경
        if (type === 'CHAPTER' && outline.chapters) {
            var newChapters = getReorderedArray(outline.chapters, source.index, destination.index);
            setStateChapters(newChapters);
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
        // outline 버퍼 상태를 mutated로 변경
        outlineBufferStatusDispatch({ type: 'mutated' });
    }
    ;
    function toggleDeleteDropAreaVisibility(type) {
        if (type !== 'PAGE' && pageDeleteDropArea.current) {
            // @ts-ignore
            pageDeleteDropArea.current.classList.toggle("visible");
            // @ts-ignore
            pageDeleteDropArea.current.classList.toggle("invisible");
        }
        else if (type === 'PAGE' && chapterDeleteDropArea.current) {
            // @ts-ignore
            chapterDeleteDropArea.current.classList.toggle("visible");
            // @ts-ignore
            chapterDeleteDropArea.current.classList.toggle("invisible");
        }
    }
    function deleteDroppedElement(type, source, destination) {
        if (window.confirm('정말로 삭제하시겠습니까?') === false)
            return;
        // 삭제 요청 전송전에, outline 업데이트 요청을 먼저 보내고 데이터를 갱신한다.
        updateOutlineOnServer(outline);
        // 삭제할 챕터의 경우
        if (type === 'CHAPTER' && outline.chapters) {
            var newChapters = Array.from(outline.chapters);
            var deletedChapter = newChapters.splice(source.index, 1)[0];
            deleteDroppedElementOnServerAndApplyFE(deletedChapter.id, type);
            // 삭제할 페이지의 경우
        }
        else if (type === 'PAGE' && outline.chapters) {
            var sourceChapter = outline.chapters.find(function (chapter) { return OutlineSidebar_1.pageDropAreaPrefix + chapter.id === source.droppableId; }); // 드래그 된 페이지가 원래 속한 챕터
            if (!sourceChapter)
                return;
            if (!sourceChapter.pages) {
                return;
            }
            var newSourcePages = Array.from(sourceChapter.pages);
            var deletedPage = newSourcePages.splice(source.index, 1)[0];
            deleteDroppedElementOnServerAndApplyFE(deletedPage.id, type);
        }
    }
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
    function deleteDroppedElementOnServerAndApplyFE(id, type) {
        if (type === 'CHAPTER') {
            axios_1["default"]["delete"]("/api/chapter/" + id)
                .then(function (response) {
                if (response.data !== undefined) {
                    flowAlert_1["default"](response.data.alertType, response.data.alert);
                    queryClient.setQueryData(["book", bookId], response.data.data);
                }
            });
        }
        else if (type === 'PAGE') {
            axios_1["default"]["delete"]("/api/page/" + id)
                .then(function (response) {
                if (response.data !== undefined) {
                    flowAlert_1["default"](response.data.alertType, response.data.alert);
                    queryClient.setQueryData(["book", bookId], response.data.data);
                }
            });
        }
        return outline;
    }
}
exports["default"] = BookEntityDraggableContext;
