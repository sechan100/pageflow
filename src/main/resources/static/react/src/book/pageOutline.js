import React from 'react';
import ReactDOM from 'react-dom';
import * as S from './styleComponents';
import { DragDropContext, Droppable, Draggable } from 'react-beautiful-dnd';



// 임시 카드 데이터입니다. 실제 데이터로 바꿀 수 있습니다.
const tempCards = [
  { id: '1', content: 'Card 1' },
  { id: '2', content: 'Card 2' }
];

function Page() {
  return (
    <DragDropContext>
      <Droppable droppableId="pageOutline">
        {(provided) => (
          <div ref={provided.innerRef} {...provided.droppableProps}>
            <ul>
              {tempCards.map((card, index) => (
                <Draggable draggableId={card.id} index={index} key={card.id}>
                  {(provided) => (
                    <div ref={provided.innerRef} {...provided.draggableProps} {...provided.dragHandleProps}>
                      <li>
                          <a href="#" class="flex p-1 items-center pl-6 w-full text-white text-sm font-normal text-gray-900 rounded-lg transition duration-75 group hover:bg-gray-700">
                              {card.content}
                          </a>
                      </li>
                    </div>
                  )}
                </Draggable>
              ))}
            </ul>
            {provided.placeholder}
          </div>
        )}
      </Droppable>
    </DragDropContext>
  );
}

document.addEventListener('DOMContentLoaded', function() {
  var outlineArea = document.getElementById('outline-dnd-area');
  if (outlineArea) {
    ReactDOM.render(React.createElement(Page, null), outlineArea);
  }
});
