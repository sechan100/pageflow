import { useRef } from 'react';
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
}


export default function OutlineSidebar(drillingProps : OutlineSidebarProps){

  const { bookId, queryClient } = drillingProps;
  const outline : Outline = useGetOutline(bookId);

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
  </OutlineSidebarWrapper>
  );
}


