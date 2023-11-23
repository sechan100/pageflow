/* eslint-disable react-hooks/exhaustive-deps */
import React, { useEffect, useLayoutEffect, useRef, useState } from 'react';
import { create } from 'zustand';
import { metaPageType, useLocationStore } from '../nav/PageCursor';




interface UseCarouselStore {
  carouselIdx: number;
  isLastCarousel: boolean;
  reserveMoveLastCarousel: boolean;
  setIsLastCarousel: (isLastCarousel : boolean) => void;
  setReserveMoveLastCarousel: (reserveMoveLastCarousel : boolean) => void;
  setCarouselIdx: (idx : number) => void;
  nextCarousel: () => void;
  prevCarousel: () => void;
}


export const useCarouselStore = create<UseCarouselStore>((set, get) => ({
  carouselIdx: 0,
  isLastCarousel: false, // 현재 캐러셀이 마지막 캐러셀'인'지 여부
  reserveMoveLastCarousel: false, // 마지막 캐러셀로 이동해야하는'지' 여부
  setIsLastCarousel: (isLastCarousel : boolean) => {
    set({ isLastCarousel })
  },
  setReserveMoveLastCarousel: (reserveMoveLastCarousel : boolean) => {
    set({ reserveMoveLastCarousel })
  },
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
  }
}));





interface CarouselProps {
  children: React.ReactNode;
  chaildrenRef: React.RefObject<HTMLDivElement>;
}


const Carousel = ({ children, chaildrenRef } : CarouselProps) => {

  const carouselRef = useRef<HTMLDivElement>(null);
  const [carouselBreakPoints, setCarouselBreakPoints] = useState<number[] | null>(null); // 캐러셀의 불연속적인 위치를 저장하는 배열 (화면 리사이즈시 업데이트)
  const { location, metaPage } = useLocationStore();
  const { carouselIdx, setCarouselIdx, setIsLastCarousel, reserveMoveLastCarousel, setReserveMoveLastCarousel, isLastCarousel} = useCarouselStore();


  // 캐러셀의 불연속적인 위치를 업데이트한다. (Page 넘어갈 때, Chapter 넘어갈 때, 화면 리사이즈시)
  useEffect(() => {
    const newBreakPoints = updateCarouselBreakPoints();
    if(newBreakPoints){
      setCarouselBreakPoints(newBreakPoints);
      console.log("새로운 BreakPoints 계산", newBreakPoints);
    }
  }, [carouselRef.current?.scrollWidth, location, metaPage.isMetaPage]);


  useEffect(() => {
    if(carouselBreakPoints){
      
      // 페이지가 전으로 이동한 경우, 전 페이지의 마지막 캐러셀로 이동한다.
      if(reserveMoveLastCarousel){
        console.log("새로운 location으로 이동하여, 마지막 캐러셀로 이동합니다.");
        setCarouselIdx(carouselBreakPoints.length - 1);
      } else {
        console.log(`새로운 location으로 이동하여, 현재 캐러셀인 ${carouselIdx}로 이동합니다.`);
        setCarouselIdx(carouselIdx);
      }
      setReserveMoveLastCarousel(false);
    }
  }, [location]);



  // PageCursor에서 zustand store의 innerPageIdx를 변화시키면 이에맞게 스크롤을 불연속적인 위치로 이동시킨다.
  useEffect(() => {
    
    if(carouselBreakPoints){
      if(carouselRef.current && carouselBreakPoints.length > 1){
        carouselRef.current.scrollTo({
          left: carouselBreakPoints[carouselIdx],
          // behavior: 'smooth',
        });
        // console.log("스크롤이동", carouselBreakPoints[carouselIdx]);
      }
  
      // 마지막 캐러셀인지 여부를 업데이트한다.
      if(carouselIdx === carouselBreakPoints.length - 1){
        // console.log("마지막 캐러셀!!!!!", true);
        setIsLastCarousel(true);
      } else {
        setIsLastCarousel(false);
      }
    }

  }, [carouselIdx]);



  // 화면 리사이즈시 스크롤 위치를 보정하는 리스너를 등록
  useEffect(() => {
    window.addEventListener('resize', handleResize);
    return () => window.removeEventListener('resize', handleResize);
  }, []);



  return (
    <div>
      <div ref={carouselRef} style={{ overflowX: 'hidden' }}>
        {children}
      </div>
    </div>
  );


  // 캐러셀의 불연속적인 위치를 업데이트한다. (화면 리사이즈시)
  function updateCarouselBreakPoints() : number[] | null {
    const carouselContainer = carouselRef.current;
    const breakPoints = [0];
    if (carouselContainer) {
      const containerWidth = carouselContainer.getBoundingClientRect().width; // 현재 텍스트가 노출되는 컨테이너의 넓이
      const unitCarousel = (containerWidth * 1.08); // 하나의 캐러셀 단위의 사이즈(column gap이 있기 때문에 containerWidth보다 살짝 크다.) 0.08은 column-gap만큼의 offset

      while(true){
        const newBreakPoint = breakPoints[breakPoints.length - 1] + unitCarousel; // 가장 마지막 브레이크 포인트에 unitCarousel만큼을 더한 값 => 다음 캐러셀 페이지

        // 만약 "(현재 스크롤 값 + (containerWidth * 1.58)"이 전체 스크롤 길이보다 길다면, isLastInnerPage를 true로 설정한다.
        // 1.58 보정값 의미: 일단 현재 스크롤 값은 가장 왼쪽까지의 스크롤된 width이기에, 현재 스크린 길이만큼을 더해준다.
        // 거기에 페이지 반절 사이즈의 offset(빈페이지 반쪽)이 존재하기 때문에, 이것까지를 더했을 때, 이미 전체 스크롤 길이 이상이라면, 다음 페이지는 존재하지 않는 것이다.
        // 그리고 0.08은 column-gap만큼의 offset값이다.
        // console.log(`${newContainerScrollLeft} + (${containerWidth} * 1.58) >= ${container.scrollWidth}`);
        if(newBreakPoint + (containerWidth * 0.58) >= carouselContainer.scrollWidth){
          break;
        } else {
          breakPoints.push(newBreakPoint);
        }
      }

      return breakPoints.length > 1 ? breakPoints : null;
    } else {
      return null;
    }
  }


  // 화면이 리사이즈됨에 따라 밀리는 스크롤을 보정하여 유지시킨다.
  function handleResize() {
    const scrollContainer = carouselRef.current;
    const viewWidth = carouselRef.current?.getBoundingClientRect().width as number; // 현재 컨텐츠 컨테이너 창 넓이
    const columnGapOffset = viewWidth*(0.08); // 0.08은 column-gap만큼의 offset

    if (scrollContainer) {
      scrollContainer.scrollLeft = (viewWidth + columnGapOffset) * (carouselIdx + 1);
      setCarouselIdx(carouselIdx + 1);
    }
  }
  

};

export default Carousel;
