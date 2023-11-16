import { MutableRefObject, useRef } from 'react';
import { Outline, ChapterSummary, PageSummary } from '../../types/types';
import BookBasicPage from '../outline/BookBasicPage';
import Chapter from './Chapter';
import OutlineSidebarWrapper from './OutlineSidebarWrapper';
import { useGetOutline } from '../../api/book-apis';
import { Draggable, Droppable } from 'react-beautiful-dnd';




export const pageDropAreaPrefix = 'pageDropArea-';
export const chapterDraggablePrefix = 'chapter-';
export const pageDraggablePrefix = 'page-';


interface OutlineSidebarProps {
  bookId : number;
  queryClient : any;
  outlineBufferStatusReducer : [
    outlineBufferStatus : string,
    outlineBufferStatusDispatch : any
  ]
}


export default function OutlineSidebar(props : OutlineSidebarProps){

  const { bookId, queryClient, outlineBufferStatusReducer } = props;
  const outline : Outline = useGetOutline(bookId);



  const openedChapterIds : MutableRefObject<string[]> = useRef([] as string[]);

  function addOpenedChapterIds(chapterId : string){
    openedChapterIds.current.push(chapterId);
  }

  function removeOpenedChapterIds(chapterId : string){
    const index = openedChapterIds.current.indexOf(chapterId);
    if (index > -1) {
      openedChapterIds.current.splice(index, 1);
    }
  }

  const chapterOpenStatus = {
    openedChapterIds,
    addOpenedChapterIds,
    removeOpenedChapterIds
  }




  return (
    <OutlineSidebarWrapper {...props}>
      <BookBasicPage bookId={outline.id}/>

      {/* 챕터 드롭 영역 */}
      <Droppable droppableId="chapter-outline" type='CHAPTER' >
        {(provided : any) => (
          <div ref={provided.innerRef} {...provided.droppableProps}>
            {/* Draggable Chapter 설정 */}
            {outline.chapters?.map((chapter, index) => (
              <Draggable key={chapterDraggablePrefix + chapter.id} draggableId={chapterDraggablePrefix + chapter.id} index={index} >
                {(provided : any) => (
                  <div ref={provided.innerRef} {...provided.draggableProps} {...provided.dragHandleProps}>

                    {/* Chapter 컴포넌트 */}
                    <Chapter chapter={chapter} chapterOpenStatus={chapterOpenStatus} />

                  </div>
                )}
              </Draggable>
            ))}

            {/* 사이즈 줄어드는 것을 방지하기 위한 plcaeholder */}
            {provided.placeholder}

          </div>
        )}
      </Droppable>
  </OutlineSidebarWrapper>
  );
}


