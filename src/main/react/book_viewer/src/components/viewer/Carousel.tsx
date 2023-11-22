/* eslint-disable react-hooks/exhaustive-deps */
import React, { useEffect, useRef } from 'react';
import { create } from 'zustand';




interface UseCarouselStore {
  carouselIdx: number;
  setCarouselIdx: (idx : number) => void;
  nextCarousel: () => void;
  prevCarousel: () => void;
  isLastCarousel: boolean;
  setIsLastCarousel: (isLastCarousel : boolean) => void;
  resetCarouselIdx: () => void;
  isMoveOverPage: boolean;
  setIsMoveOverPageTrue: () => void;
}


export const useCarouselStore = create<UseCarouselStore>((set, get) => ({
  carouselIdx: 0,
  setCarouselIdx: (idx : number) => {
    set({ carouselIdx: idx })
  },
  nextCarousel: () => {
    const idx = get().carouselIdx;
    set({ carouselIdx: idx + 1 })
  },
  prevCarousel: () => {
    const idx = get().carouselIdx;
    set({ carouselIdx: idx - 1 })
  },
  isLastCarousel: false, // 현재의 캐러셀이 스크롤의 불연속 지점의 마지막인지 여부
  setIsLastCarousel: (isLastCarousel : boolean) => {
    set({ isLastCarousel: isLastCarousel })
  },
  resetCarouselIdx: () => {
    set({ 
      carouselIdx: 0,
      isLastCarousel: false
    })
  }, 
  isMoveOverPage: false, // 바로 직전에 바뀐 캐러셀이, page를 넘어가면서 변화한 것인지의 여부 (page를 넘어가면서 변화한 캐러셀이라면 애니메이션, 새로운 스크롤 길이등을 따로 처리해야함)
  setIsMoveOverPageTrue: () => {
    set({ isMoveOverPage: true })
  },
}));





interface CarouselProps {
  children: React.ReactNode;
  container: React.RefObject<HTMLDivElement>;
}


const Carousel = ({ children, container } : CarouselProps) => {

  const carouselRef = useRef<HTMLDivElement>(null);
  const {carouselIdx, setCarouselIdx, isLastCarousel, setIsLastCarousel} = useCarouselStore();



  // PageCursor에서 zustand store의 innerPageIdx를 변화시키면 이에맞게 스크롤을 불연속적인 위치로 이동시킨다.
  useEffect(() => {
    scrollToBreakPoint(carouselIdx);
  }, [carouselIdx]);


  // 화면 리사이즈시 스크롤 위치를 보정하는 리스너를 등록
  useEffect(() => {
    window.addEventListener('resize', handleResize);
    return () => window.removeEventListener('resize', handleResize);
  }, []);




  return (
    <div>
      <div ref={carouselRef} style={{ overflowX: 'auto' }}>
        {children}
      </div>
    </div>
  );

    // 캐러셀의 x축 scroll을 page 단위의 불연속적인 지점으로 움직인다.
    function scrollToBreakPoint(index : number) {
      const container = carouselRef.current;
      if (container) {
        const containerWidth = container.getBoundingClientRect().width as number; // 현재 텍스트가 노출되는 컨테이너의 넓이
        const newContainerScrollLeft = (containerWidth * 1.08) * index; // 0.08은 column-gap만큼의 offset
        container.scrollTo({
          left: newContainerScrollLeft,
          // behavior: 'smooth',
        });


        // 컨테이너 x축 스크롤 넓이가 모두 로딩되지 않았다면 실행하지 않음
        if(container.scrollWidth > containerWidth){
          
          // 만약 "(현재 스크롤 값 + (containerWidth * 1.58)"이 전체 스크롤 길이보다 길다면, isLastInnerPage를 true로 설정한다.
          // 1.58 보정값 의미: 일단 현재 스크롤 값은 가장 왼쪽까지의 스크롤된 width이기에, 현재 스크린 길이만큼을 더해준다.
          // 거기에 페이지 반절 사이즈의 offset(빈페이지 반쪽)이 존재하기 때문에, 이것까지를 더했을 때, 이미 전체 스크롤 길이 이상이라면, 다음 페이지는 존재하지 않는 것이다.
          // 그리고 0.08은 column-gap만큼의 offset값이다.

          // console.log(`${newContainerScrollLeft} + (${containerWidth} * 1.58) >= ${container.scrollWidth}`);

          if (newContainerScrollLeft + (containerWidth * 1.58) >= container.scrollWidth) {
            setIsLastCarousel(true);
          }
        }
      }
    };

  // 화면이 리사이즈됨에 따라 밀리는 스크롤을 보정하여 유지시킨다.
  function handleResize() {
    const scrollContainer = carouselRef.current;
    const viewWidth = container.current?.getBoundingClientRect().width as number; // 현재 컨텐츠 컨테이너 창 넓이
    const columnGapOffset = viewWidth*(0.08); // 0.08은 column-gap만큼의 offset

    if (scrollContainer) {
      scrollContainer.scrollLeft = (viewWidth + columnGapOffset) * (carouselIdx + 1);
      setCarouselIdx(carouselIdx + 1);
    }
  }
  
};

export default Carousel;
