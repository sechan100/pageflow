import { DragDropContext, Droppable } from "react-beautiful-dnd";
import OutlineSidebar, { pageDropAreaPrefix } from "./outline/OutlineSidebar";
import { ChapterSummary, Outline, PageSummary } from "../types/types";
import { useGetOutline, useRearrangeOutlineMutation } from "../api/book-apis";
import { useEffect, useReducer, useRef } from "react";
import { inClosingPageDropAreaPrefix } from "./outline/Chapter";
import axios from "axios";
import  flowAlert  from "../etc/flowAlert";
import FormMain from "./form/FormMain";




interface BookEntityDraggableContextProps {
  bookId : number;
  queryClient : any;
}


export default function BookEntityDraggableContext(props : BookEntityDraggableContextProps) {

  const { bookId, queryClient } = props;
  // react query로 server book outline snapshot을 가져온다.
  const outline : Outline = useGetOutline(bookId);
  const chapterDeleteDropArea = useRef(null); // Chapter 삭제 드롭 영역의 DOM 참조
  const pageDeleteDropArea = useRef(null); // Page 삭제 드롭 영역의 DOM 참조

  // outline 재정렬 여부에 대한 상태를 기록하고, 변경된 데이터 버퍼를 전송할지 말지에 관한 상태를 나타낸다.
  const [outlineBufferStatus, outlineBufferStatusDispatch] = useReducer((status : string, action : {type:string}) => {

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

  }, "flushed");


  const { mutateAsync, isLoading, error } = useRearrangeOutlineMutation(bookId);

  // 서버에 Outline 데이터의 재정렬 업데이트 요청을 보내는 함수
  async function updateOutlineOnServer(outline : Outline){

    // outlineBufferStatus가 mutated, waiting인 경우에만 서버에 요청을 보낸다.
    if(outlineBufferStatus === 'mutated' || outlineBufferStatus === 'waiting'){

      try{

        await mutateAsync(outline)
        flowAlert('success', "목차 정보가 저장되었습니다.");

        // 요청을 전달한 후에 성공적으로 업데이트 되었다면, outlineBufferStatus를 flushed로 변경한다.
        outlineBufferStatusDispatch({type: 'flushed'});

      } catch(error) {
        flowAlert('error', "목차 정보를 서버와 동기화하지 못했습니다.");
      }
    }
  }

  // outlineBuffer가 변경될 때마다 5초 뒤 서버에 재정렬 요청을 보내는 타이머를 시작, 도중에 outlineBuffer가 변경되면 타이머를 초기화한다.
  useEffect(() => {
    
    // outlineBufferStatus가 mutated인 경우, 업데이트 요청을 전송하기위한 타이머를 시작한다.
    if(outlineBufferStatus === 'mutated'){

      const outlineBufferFlushTimer = setTimeout(async () => {
        updateOutlineOnServer(outline);
      }, 7000);

      return () => {
        clearTimeout(outlineBufferFlushTimer);
      }
    }

  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [outlineBufferStatus]);




  return (
    <>
      <DragDropContext onDragStart={onDragStart}  onDragEnd={onDragEnd}>
        <OutlineSidebar {...props} outlineBufferStatusReducer={[outlineBufferStatus, outlineBufferStatusDispatch]} />

        {/* 삭제할 요소를 드롭 */}
        <Droppable droppableId="chapter-delete-drop-area" type="CHAPTER">
          {(provided, snapshot) => (
            <div className="bg-gray-500 animate-bounce hover:bg-gray-700 w-48 absolute invisible left-1/2 top-5 p-5 px-6 rounded-full" ref={chapterDeleteDropArea}>
              <div ref={provided.innerRef} {...provided.droppableProps} className="flex">
                <svg className="w-6 h-6 text-gray-800 dark:text-white mr-3" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 18 20">
                  <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M1 5h16M7 8v8m4-8v8M7 1h4a1 1 0 0 1 1 1v3H6V2a1 1 0 0 1 1-1ZM3 5h12v13a1 1 0 0 1-1 1H4a1 1 0 0 1-1-1V5Z"/>
                </svg>
                <span className="text-white">드래그하여 삭제</span>
              </div>
            </div>
          )}
        </Droppable> 

        {/* 삭제할 요소를 드롭 */}
        <Droppable droppableId="page-delete-drop-area" type="PAGE">
          {(provided, snapshot) => (
            <div className="bg-gray-500 animate-bounce hover:bg-gray-700 w-48 absolute invisible left-1/2 top-5 p-5 px-6 rounded-full" ref={pageDeleteDropArea}>
              <div ref={provided.innerRef} {...provided.droppableProps} className="flex">
                <svg className="w-6 h-6 text-gray-800 dark:text-white mr-3" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 18 20">
                  <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M1 5h16M7 8v8m4-8v8M7 1h4a1 1 0 0 1 1 1v3H6V2a1 1 0 0 1 1-1ZM3 5h12v13a1 1 0 0 1-1 1H4a1 1 0 0 1-1-1V5Z"/>
                </svg>
                <span className="text-white">드래그하여 삭제</span>
              </div>
            </div>
          )}
        </Droppable>

        <FormMain {...props} />
      </DragDropContext>
    </>
  );



  function onDragStart(start : any) {
    toggleDeleteDropAreaVisibility(start.type);
    
    // 만약 이전에 mutated였다면, 버퍼 전송을 flush하지는 않지만 잠시 멈춰두기 위해서 waiting으로 변경한다.
    if(outlineBufferStatus === 'mutated'){
      outlineBufferStatusDispatch({type: 'waiting'});
    }

  };

  function onDragEnd(result: any){

    toggleDeleteDropAreaVisibility(result.type);

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
      deleteDroppedElement(type, source, destination);
      return;
    }


    // re-order: 1. 챕터간의 순서 변경
    if (type === 'CHAPTER' && outline.chapters) {
      const newChapters = getReorderedArray(outline.chapters, source.index, destination.index);
      setStateChapters(newChapters)

    // 페이지 변경
    } else if (type === 'PAGE' && outline.chapters) {

      // 닫힌 상태의 챕터에 드롭되었는지 여부
      const isInClosingPageDropAreaDropped : boolean = destination.droppableId.startsWith(inClosingPageDropAreaPrefix);

      // 출발지와 목적지의 챕터를 가져옴
      const [sourceChapter, destinationChapter] = getSourceChapterAndDestinationChapter(source.droppableId, destination.droppableId, isInClosingPageDropAreaDropped);

      // 올바른 드롭 이벤트가 아닌 경우 종료.
      if(!sourceChapter || !destinationChapter) return;

      if(!sourceChapter.pages || !destinationChapter.pages){
        return;
      }

      // re-order: 2. 페이지의 챕터 내부 순서 변경 (출발지와 목적지가 같은 경우)
      if(sourceChapter === destinationChapter) {
        const newPages = getReorderedArray(sourceChapter.pages, source.index, destination.index);
        setStatePages(newPages, sourceChapter.id);

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

        const newChapters = outline.chapters.map(chapter => {

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

        setStateChapters(newChapters);
      }
    }
    // outline 버퍼 상태를 mutated로 변경
    outlineBufferStatusDispatch({type: 'mutated'});
  };

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

  function deleteDroppedElement(type : string, source : any, destination : any){
    
    if(window.confirm('정말로 삭제하시겠습니까?') === false) return;

    // 삭제 요청 전송전에, outline 업데이트 요청을 먼저 보내고 데이터를 갱신한다.
    updateOutlineOnServer(outline);

    // 삭제할 챕터의 경우
    if(type === 'CHAPTER' && outline.chapters){

      const newChapters = Array.from(outline.chapters);
      const deletedChapter = newChapters.splice(source.index, 1)[0];
      deleteDroppedElementOnServerAndApplyFE(deletedChapter.id, type);

    // 삭제할 페이지의 경우
    } else if (type === 'PAGE' && outline.chapters) {

      const sourceChapter = outline.chapters.find(chapter => pageDropAreaPrefix + chapter.id === source.droppableId); // 드래그 된 페이지가 원래 속한 챕터

      if(!sourceChapter) return;

      if(!sourceChapter.pages){
        return;
      }

      const newSourcePages = Array.from(sourceChapter.pages);
      const deletedPage = newSourcePages.splice(source.index, 1)[0];
      deleteDroppedElementOnServerAndApplyFE(deletedPage.id, type);

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

    if(!outline.chapters) return [null, null];

    const sourceChapter = outline.chapters.find(chapter => pageDropAreaPrefix + chapter.id === sourceChapterDroppableId); // 드래그 된 페이지가 원래 속한 챕터
    let destinationChapter;

    // 닫혀있는 챕터로 destinationChapter를 찾음
    if(isInClosingPageDropAreaDropped){
      destinationChapter = outline.chapters.find(chapter => inClosingPageDropAreaPrefix + String(chapter.id) === destinationChapterDroppableId);

    // 열린 상태인 챕터로 destinationChapter를 찾음
    } else {
      destinationChapter = outline.chapters.find(chapter => pageDropAreaPrefix + String(chapter.id) === destinationChapterDroppableId);
    }

    if(sourceChapter && destinationChapter) {
      return [sourceChapter, destinationChapter];
    } else {
      console.log('sourceChapter, 또는 destinationChapter가 정의되지 않았습니다.');
      return [null, null];
    }

  }

      // queryClient의 ["book", bookId]로 저장된 서버 스냅샷을 업데이트한다. 
  function setStateChapters(newChapters : ChapterSummary[]){
    const newOutline : Outline = {
      ...outline,
      chapters: newChapters
    }
    queryClient.setQueryData(["book", bookId], newOutline);
  }

  // queryClient의 ["book", bookId]로 저장된 서버 스냅샷을 업데이트한다. 
  function setStatePages(pages : PageSummary[], sourceChapterId : number){
    if(!outline.chapters) return;


    const newChapters = outline.chapters.map(chapter => {

      // 변경된 페이지를 가지는 챕터는 새로운 페이지로 교체
      if (chapter.id === sourceChapterId) {
        return {
          ...chapter,
          pages: pages
        }

      // 나머지 페이지는 유지
      } else {
        return chapter;
      }

    });

    setStateChapters(newChapters);
  }

  function deleteDroppedElementOnServerAndApplyFE(id : number, type : string) : Outline {
    if(type === 'CHAPTER'){
      axios.delete(`/api/chapter/${id}`)
      .then(response => {
        if(response.data !== undefined){
          flowAlert(response.data.alertType, response.data.alert);
          queryClient.setQueryData(["book", bookId], response.data.data);
        }
      })

    } else if (type === 'PAGE'){
      axios.delete(`/api/page/${id}`)
      .then(response => {
        if(response.data !== undefined){
          flowAlert(response.data.alertType, response.data.alert);
          queryClient.setQueryData(["book", bookId], response.data.data);
        }
      })
    }

    return outline;
  }




}