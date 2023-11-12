import { DragDropContext, Droppable, Draggable } from 'react-beautiful-dnd';
import { useState } from 'react';
import { Outline, ChapterSummary, PageSummary } from '../../types/types';
import BookBasicPage from '../outline/BookBasicPage';
import Chapter from './Chapter';
import OutlineSidebarWrapper from './OutlineSidebarWrapper';
import { inClosingPageDropAreaPrefix } from './Chapter';
import { useGetOutline } from '../../api/book-apis';



export const pageDropAreaPrefix = 'pageDropArea-';
export const chapterDraggablePrefix = 'chapter-';
export const pageDraggablePrefix = 'page-';


interface OutlineSidebarProps {
  bookId : number;
  queryClient : any;
}


export default function OutlineSidebar(drillingProps : OutlineSidebarProps){

  const { bookId, queryClient } = drillingProps;

  // react query로 server book outline snapshot을 가져온다.
  const outline : Outline = useGetOutline(bookId);


  // 챕터의 드롭영역을 한정하기 위한 state; bookOutline droppable 영역의 isDropDisabled 속성에 사용된다.
  const [isChapterDragging, setIsChapterDragging] = useState(false);


  // 특정 타겟의 'dragging' state를 true로 변경
  const turnOnTargetDraggingState = (start : any) => {
    const isChapter = start.type === 'CHAPTER';
    setIsChapterDragging(isChapter);
  };

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

  const onDragEnd = (result: any) => {
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

    // re-order: 1. 챕터간의 순서 변경
    if (type === 'CHAPTER' && outline.chapters) {
      setIsChapterDragging(false); // 챕터의 드래그 상태를 종료.
      const newChapters = getReorderedArray(outline.chapters, source.index, destination.index);
      setStateChapters(newChapters)
      return;

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
  };

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


  return (
    <OutlineSidebarWrapper {...drillingProps}>
      <BookBasicPage bookId={outline.id}/>
      <DragDropContext onDragStart={turnOnTargetDraggingState} onDragEnd={onDragEnd}>

        {/* 챕터 드롭 영역 */}
        <Droppable droppableId="chapter-outline" isDropDisabled={!isChapterDragging} type='CHAPTER' >
          {(provided : any) => (
            <div ref={provided.innerRef} {...provided.droppableProps}>
              {/* Draggable Chapter 설정 */}
              {outline.chapters?.map((chapter, index) => (
                <Draggable key={chapterDraggablePrefix + chapter.id} draggableId={chapterDraggablePrefix + chapter.id} index={index} >
                  {(provided : any) => (
                    <div ref={provided.innerRef} {...provided.draggableProps} {...provided.dragHandleProps}>

                      {/* Chapter 컴포넌트 */}
                      <Chapter chapter={chapter} />

                    </div>
                  )}
                </Draggable>
              ))}

              {/* 사이즈 줄어드는 것을 방지하기 위한 plcaeholder */}
              {provided.placeholder}

            </div>
          )}
        </Droppable>

      </DragDropContext>
  </OutlineSidebarWrapper>
  );
}


