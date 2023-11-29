/* eslint-disable react-hooks/exhaustive-deps */
import { useContext, useEffect, useReducer, useRef } from "react";
import { useGetOutlineQuery } from "../api/outline-api";
import { ChapterSummary, Outline, OutlineMutation, PageSummary } from "../types/types";
import { DragDropContext, Droppable } from "react-beautiful-dnd";
import { inClosingPageDropAreaPrefix } from "./outline/items/Chapter";
import { QueryContext } from "../App";
import OutlineContext from "./outline/OutlineContext";
import FormMain from "./form/FormMain";
import { pageDropAreaPrefix } from "./outline/OutlineContext";
import { create } from "zustand";
import flowAlert from "../etc/flowAlert";
import { useAutoSaveStore } from "./saveBtn/MutationSaveBtn";


interface UseOutlineMutationStore {
  payload: OutlineMutation;
  isMutated: boolean;
  resetMutation: () => void;
  isLoading: boolean;
  dispatchs: {
    setChapters : (chapters : ChapterSummary[]) => void
  };
}



export const useOutlineMutationStore = create<UseOutlineMutationStore>((set : any) => ({
  payload: { chapters: null }, // 목차 재배열 변경사항을 서버에 요청하기 위한 객체
  isMutated: false, // 변경사항이 존재하는지 여부
  resetMutation: () => set(
    (state : any) => ({
      ...state,
      payload: { chapters: null },
      isMutated: false
    })
  ),
  isLoading: false, // 서버에 전달한 요청이 진행중인지의 여부. 기본적으로 false이지만, [true로 바뀌는 순간부터, 다시 false가 되는 순간]까지 서버에 요청중임을 의미한다.
  dispatchs: { // payload 상태를 변경하는 dispatch 함수들. 내부적으로 isMutated를 true로 변경하는 로직을 포함한다.

    setChapters : (chapters : ChapterSummary[]) => {
      set((state : any) => ({
        payload: {
          ...state.payload,
          chapters: chapters
        },
        isMutated: true
      }));
    }

  },
}));



// 해당 컴포넌트는 페이지 전역에 걸친 DragDropContext를 제공한다. -> 삭제 드롭 영역을 Form 페이지 위에 나타내야하기 때문에 거의 전체 페이지에 걸쳐서 DragDropContext를 제공해야하므로..
export default function BookEntityDraggableContext() {
  
  const { bookId } = useContext(QueryContext);
  const outline: Outline = useGetOutlineQuery(bookId);
  const [localOutline, localOutlineDispatch] : [Outline, any] = useReducer(localOutlineReducer, outline); // zustand store에 변경사항을 업데이트하기 전에 임시로 저장하는 로컬 상태
  const { isAutoSaveAvailable } = useAutoSaveStore(); // 자동 저장 비활성화

  // outline query data -> localOutline 상태에 동기화
  useEffect(() => {
    if(outline){
      localOutlineDispatch({type: 'SET_OUTLINE', payload: outline});
    }
  }, [outline]);

  // Outline 변경사항에 관한 zusatnd store
  const { dispatchs } = useOutlineMutationStore();

  // fallback 데이터가 아니면서 && localOutline에 변경사항이 적용된 경우...
  useEffect(() => {
    if(localOutline.id !== 0 && outline.chapters !== localOutline.chapters){
      dispatchs.setChapters(localOutline.chapters as ChapterSummary[]);
    }
  }, [localOutline]);




  // 두 Draggable 객체의 type이 각각 다르기 때문에, 각각에 해당하는 삭제 드롭 영역을 겹쳐서 놓아야한다.
  // 그리고 실제로 드래그 된 요소의 type에 일치하는 영역만을 visible하게 만든다. 실제로는 2개가 겹쳐져 있는 것.
  const chapterDeleteDropArea = useRef(null); // Chapter 삭제 드롭 영역의 DOM 참조
  const pageDeleteDropArea = useRef(null); // Page 삭제 드롭 영역의 DOM 참조


  return (
    <>
      <DragDropContext onDragStart={onDragStart}  onDragEnd={onDragEnd}>
        <OutlineContext outline={localOutline} />

        {/* 삭제할 요소를 드롭 */}
        <Droppable droppableId="chapter-delete-drop-area" type="CHAPTER">
          {(provided, snapshot) => (
            <div className="bg-gray-500 z-30 animate-bounce hover:bg-gray-700 w-48 absolute invisible left-1/2 top-5 p-5 px-6 rounded-full" ref={chapterDeleteDropArea}>
              <div ref={provided.innerRef} {...provided.droppableProps} className="flex">
                <svg className="w-6 h-6 text-gray-800 dark:text-white mr-3" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 18 20">
                  <path stroke="currentColor" strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M1 5h16M7 8v8m4-8v8M7 1h4a1 1 0 0 1 1 1v3H6V2a1 1 0 0 1 1-1ZM3 5h12v13a1 1 0 0 1-1 1H4a1 1 0 0 1-1-1V5Z"/>
                </svg>
                <span className="text-white">드래그하여 삭제</span>
              </div>
            </div>
          )}
        </Droppable> 

        {/* 삭제할 요소를 드롭 */}
        <Droppable droppableId="page-delete-drop-area" type="PAGE">
          {(provided, snapshot) => (
            <div className="bg-gray-500 z-30 animate-bounce hover:bg-gray-700 w-48 absolute invisible left-1/2 top-5 p-5 px-6 rounded-full" ref={pageDeleteDropArea}>
              <div ref={provided.innerRef} {...provided.droppableProps} className="flex">
                <svg className="w-6 h-6 text-gray-800 dark:text-white mr-3" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 18 20">
                  <path stroke="currentColor" strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M1 5h16M7 8v8m4-8v8M7 1h4a1 1 0 0 1 1 1v3H6V2a1 1 0 0 1 1-1ZM3 5h12v13a1 1 0 0 1-1 1H4a1 1 0 0 1-1-1V5Z"/>
                </svg>
                <span className="text-white">드래그하여 삭제</span>
              </div>
            </div>
          )}
        </Droppable>

        <FormMain />
      </DragDropContext>
    </>
  );



  function onDragStart(start : any) {
    toggleDeleteDropAreaVisibility(start.type);
    isAutoSaveAvailable.current = false; // 자동 저장 비활성화
  }


  function onDragEnd(result: any){
    toggleDeleteDropAreaVisibility(result.type);
    isAutoSaveAvailable.current = true; // 자동 저장 활성화
    
    const {
      destination, // 최종 드롭된 위치(목적지)
      source,     // 기존 위치(출발지)
      type        // 드래그된 요소의 타입(특정 타입이 적용된 Droppable의 직계 자식은 해당 타입을 가진다.)
    } = result;

    if (!destination) {
      return;
    }
  
    // 원래 위치와 동일한 위치로 드래그 되었을 경우 state를 유지.
    if (destination.droppableId === source.droppableId && destination.index === source.index) {
      return;
    }

    // 삭제 영역으로 드롭된 경우 삭제 로직 실행
    if(destination.droppableId === 'chapter-delete-drop-area' || destination.droppableId === 'page-delete-drop-area'){
      deleteDroppedElement(type, source);
      return;
    }

    // re-order: 1. 챕터간의 순서 변경
    if (type === 'CHAPTER' && localOutline.chapters) {
      const newChapters = getReorderedArray(localOutline.chapters, source.index, destination.index);
      localOutlineDispatch({type: 'SET_CHAPTERS', payload: newChapters});

    // 페이지 변경
    } else if (type === 'PAGE' && localOutline.chapters) {

      // 닫힌 상태의 챕터에 드롭되었는지 여부
      const isInClosingPageDropAreaDropped : boolean = destination.droppableId.startsWith(inClosingPageDropAreaPrefix);

      // 출발지와 목적지의 챕터를 가져옴
      const [sourceChapter, destinationChapter] = getSourceChapterAndDestinationChapter(source.droppableId, destination.droppableId, isInClosingPageDropAreaDropped);

      // 올바른 드롭 이벤트가 아닌 경우 종료.
      if(!sourceChapter || !destinationChapter) return;

      // 출발지 또는 목적지의 챕터가 페이지를 가지고 있지 않은 경우 종료.
      if(!sourceChapter.pages || !destinationChapter.pages){
        return;
      }

      if(sourceChapter.pages.length === 1){
        flowAlert("warning", '최소 1개의 페이지가 필요합니다.');
        return;
      }

      // re-order: 2. 페이지의 챕터 내부 순서 변경 (출발지와 목적지가 같은 경우)
      if(sourceChapter === destinationChapter) {
        const newPages = getReorderedArray(sourceChapter.pages, source.index, destination.index);
        localOutlineDispatch({type: 'SET_PAGES', payload: newPages, chapterId: sourceChapter.id});

      // re-order: 3. 페이지의 챕터간 이동
      } else if (sourceChapter !== destinationChapter) {
        const newSourcePages = Array.from(sourceChapter.pages);
        const newDestinationPages = Array.from(destinationChapter.pages);
        const [removedPage] = newSourcePages.splice(source.index, 1);


        // re-order: 3-1. 닫혀있는 상태의 챕터로 드롭된 경우 -> 해당 챕터의 제일 마지막 인덱스로 추가.
        if(isInClosingPageDropAreaDropped){
          newDestinationPages.splice(destinationChapter.pages.length, 0, removedPage);

        // re-order: 3-2. 챕터가 열려있는 상태에서 순서까지 같이 지정하여 드롭한 경우. -> 지정된 index 위치로 추가.
        } else {
          newDestinationPages.splice(destination.index, 0, removedPage);
        }

        const newSourceChapter = {
          ...sourceChapter,
          pages: newSourcePages
        }

        const newDestinationChapter = {
          ...destinationChapter,
          pages: newDestinationPages
        }

        const newChapters = localOutline.chapters.map(chapter => {

          // sourceChapter인 경우, 새로운 newSourceChapter로 교체
          if (chapter.id === newSourceChapter.id) {
            return newSourceChapter;

          // destinationChapter 경우, 새로운 newDestinationChapter 교체
          } else if (chapter.id === newDestinationChapter.id) {
            return newDestinationChapter;

          // 나머지는 그대로 유지
          } else {
            return chapter;
          }
        });

        localOutlineDispatch({type: 'SET_CHAPTERS', payload: newChapters});
      }
    }
  }


  function toggleDeleteDropAreaVisibility(type : string){
    if(type !== 'PAGE' && pageDeleteDropArea.current){
      // @ts-ignore
      pageDeleteDropArea.current.classList.toggle("visible");
      // @ts-ignore
      pageDeleteDropArea.current.classList.toggle("invisible");
    } else if(type === 'PAGE' && chapterDeleteDropArea.current){
      // @ts-ignore
      chapterDeleteDropArea.current.classList.toggle("visible");
      // @ts-ignore
      chapterDeleteDropArea.current.classList.toggle("invisible");
    }
  }


  function deleteDroppedElement(type : string, source : any){
    
    if(window.confirm('정말로 삭제하시겠습니까?') === false) return;

    // 삭제할 챕터의 경우
    if(type === 'CHAPTER' && localOutline.chapters){

      if(localOutline.chapters.length === 1){
        flowAlert("warning", '최소 1개의 챕터가 필요합니다.');
        return;
      }

      const newChapters = Array.from(localOutline.chapters);
      newChapters.splice(source.index, 1);
      localOutlineDispatch({type: 'SET_CHAPTERS', payload: newChapters});

    // 삭제할 페이지의 경우
    } else if (type === 'PAGE' && localOutline.chapters) {

      const sourceChapter = localOutline.chapters.find(chapter => pageDropAreaPrefix + chapter.id === source.droppableId); // 드래그 된 페이지가 원래 속한 챕터

      if(!sourceChapter) return;

      if(sourceChapter.pages && sourceChapter.pages.length === 1){
        flowAlert("warning", '최소 1개의 페이지가 필요합니다.');
        return;
      }

      if(!sourceChapter.pages){
        return;
      }

      const newSourcePages = Array.from(sourceChapter.pages);
      newSourcePages.splice(source.index, 1);
      sourceChapter.pages = newSourcePages;

      const newChapters = localOutline.chapters.map(chapter => {
        if (chapter.id === sourceChapter.id) {
          return sourceChapter;
        } else {
          return chapter;
        }
      });

      localOutlineDispatch({type: 'SET_CHAPTERS', payload: newChapters});
    }
  }


  // 첫 매개변수로 받은 배열의 sourceIndex와 destinationIndex의 요소를 서로 교환하여 반환한다.
  function getReorderedArray<T>(array: T[], sourceIndex: number, destinationIndex: number) {

    const newArray = Array.from(array);

    const [removedElement] = newArray.splice(sourceIndex, 1); // 기존 위치의 요소를 제거
    newArray.splice(destinationIndex, 0, removedElement); // 새로운 위치에 요소를 삽입

    return newArray;
  }


  // DroppableId로부터 sourceChapter와 destinationChapter를 가져온다; destinationChapter가 닫힌 상태의 챕터라면, DrppableId가 다르기 때문에, 닫힌 챕터에 드롭되었는지의 여부도 인자로 받는다.
  function getSourceChapterAndDestinationChapter(sourceChapterDroppableId : string, destinationChapterDroppableId : string, isInClosingPageDropAreaDropped : boolean){

    if(!localOutline.chapters) return [null, null];

    const sourceChapter = localOutline.chapters.find(chapter => pageDropAreaPrefix + chapter.id === sourceChapterDroppableId); // 드래그 된 페이지가 원래 속한 챕터
    let destinationChapter;

    // 닫혀있는 챕터로 destinationChapter를 찾음
    if(isInClosingPageDropAreaDropped){
      destinationChapter = localOutline.chapters.find(chapter => inClosingPageDropAreaPrefix + String(chapter.id) === destinationChapterDroppableId);

    // 열린 상태인 챕터로 destinationChapter를 찾음
    } else {
      destinationChapter = localOutline.chapters.find(chapter => pageDropAreaPrefix + String(chapter.id) === destinationChapterDroppableId);
    }

    if(sourceChapter && destinationChapter) {
      return [sourceChapter, destinationChapter];
    } else {
      console.log('sourceChapter, 또는 destinationChapter가 정의되지 않았습니다.');
      return [null, null];
    }

  }


  /* action : { 
    type : string, // 'SET_CHAPTERS' | 'SET_PAGES'
    payload : ChapterSummary[] | PageSummary[], Chapter배열 또는 Page배열
    chapterId?: number // action.type이 'SET_PAGES'인 경우,변경된 페이지를 가지게될 챕터의 id
  } */
  function localOutlineReducer(state : Outline, action : { type : string, payload : ChapterSummary[] | PageSummary[] | Outline, chapterId?: number }) : Outline {
    if(!state.chapters) return state;
    switch(action.type){
      case 'SET_CHAPTERS':
        return {
          ...state,
          chapters: action.payload as ChapterSummary[]
        }
      case 'SET_PAGES': 
        return {
          ...state,
          chapters: state.chapters.map(chapter => {
            // 변경된 페이지를 가지는 챕터는 새로운 페이지로 교체
            if (chapter.id === action.chapterId) {
              return {
                ...chapter,
                pages: action.payload as PageSummary[]
              }
            // 나머지 페이지는 유지
            } else {
              return chapter;
            }
          })
        }
      case 'SET_OUTLINE':
        return action.payload as Outline;
      default:
        return state;
    }
  }
}