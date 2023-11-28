/* eslint-disable react-hooks/exhaustive-deps */
import React, { useEffect, useRef } from 'react';
import { create } from 'zustand';
import _ from 'lodash';




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
  carouselContentRef: React.RefObject<HTMLDivElement>;
}


const Carousel = ({ children, carouselContentRef } : CarouselProps) => {

  const carouselRef = useRef<HTMLDivElement>(null);
  const carouselBreakPointsRef = useRef<number[] | null>(null); // 캐러셀의 불연속적인 위치를 저장하는 배열 (화면 리사이즈시 업데이트)
  const { carouselIdx, setCarouselIdx, setIsLastCarousel, reserveMoveLastCarousel, setReserveMoveLastCarousel, isLastCarousel} = useCarouselStore();



  // 캐러셀의 불연속적인 위치를 업데이트한다. (Page 넘어갈 때, Chapter 넘어갈 때, 화면 리사이즈시)
  useEffect(() => {
    
    const updateBreakPoints = () => {
      const newBreakPoints = updateCarouselBreakPoints();
      if(newBreakPoints){ // 새로 계산된 BreakPoints가 null이라면, [0]배열이라는 뜻... 즉, 아직 캐러셀 스크롤 로딩이 안된 상태이다.
        const carouselBreakPoints = carouselBreakPointsRef.current;
        // 이전의 BreakPoints가 null이라면 값이 처음 초기화되는 것으로, 바로 업데이트한다.
        if(!carouselBreakPoints){
          carouselBreakPointsRef.current = newBreakPoints;
          console.log("BreakPoints를 초기화했습니다.", newBreakPoints);
  
        // 만약, 이전에 BreakPoints가 null이 아닌데, 새로운 BreakPoints가 계산되었다면, 내용비교후에 다르다면 업데이트한다.
        } else if(!areArraysEqual(carouselBreakPoints, newBreakPoints)){
          carouselBreakPointsRef.current = newBreakPoints;
          console.log("BreakPoints를 업데이트 했습니다.", newBreakPoints);
        }
        return newBreakPoints;
      }
    }

    const cssAndImgLoadedTimeout = () => {
      return setTimeout(() => {

        // CSS 적용 후 수행할 작업
        const newBps = updateBreakPoints();

        if(newBps){
          // Location이 바뀌었을 때, reserveMoveLastCarousel이 true라면, 이전 페이지로 이동하였다는 의미이다. 따라서 이전 페이지의 마지막 캐러셀로 이동시킨다.
          if(reserveMoveLastCarousel){
            setCarouselIdx(newBps.length - 1);
          } else {
            setCarouselIdx(carouselIdx);
          }
          setReserveMoveLastCarousel(false); // reserveMoveLastCarousel을 reset
        }

      }, 0);
    }

    const images = carouselContentRef.current?.querySelectorAll('img') as NodeListOf<HTMLImageElement>;
    // metaPage인 경우.
    if(!images){
      carouselBreakPointsRef.current = null;
      setReserveMoveLastCarousel(false);
      return;
    }

    let loadedImages = 0; // 로드된 이미지 개수
  
    const onImageLoaded = () => {
      loadedImages += 1;

      // 모든 이미지가 로드되었을 때...
      if (loadedImages === images.length) {
        cssAndImgLoadedTimeout();
      }
    };
  
    images.forEach(img => {
      if (img.complete) {
        loadedImages += 1;
      } else {
        img.addEventListener('load', onImageLoaded);
        img.addEventListener('error', onImageLoaded); // 에러 처리
      }
    });
  
    if (loadedImages === images.length) {
      cssAndImgLoadedTimeout();
    }
  
    // 클린업 함수
    return () => {
      images.forEach(img => {
        img.removeEventListener('load', onImageLoaded);
        img.removeEventListener('error', onImageLoaded);
      });
    };


  }, [carouselRef.current]);




  //carouselIdx가 변화하면, carouselBreakPoints 배열 값에 맞춰서 스크롤을 불연속적인 위치로 이동시킨다.
  useEffect(() => {
    const bps = carouselBreakPointsRef.current;
    if(bps){
      let logMsg = "";
      if(carouselRef.current){
        carouselRef.current.scrollTo({
          left: bps[carouselIdx],
          // behavior: 'smooth',
        });
        logMsg += `캐러셀 인덱스 ${carouselIdx}로 이동`;
      }
  
      // 마지막 캐러셀인지 여부를 업데이트한다.
      if(carouselIdx === bps.length - 1){
        logMsg += "하였고, 마지막 캐러셀입니다.";
        setIsLastCarousel(true);

      // 마지막 캐러셀이 아닌데도, isLastCarousel이 true인 경우, false로 업데이트한다.
      // 주로, 마지막 캐러셀에서 이전 캐러셀로 이동한 경우, 이런 상황이 발생.
      } else if(isLastCarousel) { 
        setIsLastCarousel(false);
        logMsg += "했습니다.";
      } else {
        logMsg += "했습니다.";
      }
      console.log(logMsg);
    }

  }, [carouselIdx]);



  // 화면 리사이즈시 스크롤 위치를 보정하는 리스너를 등록
  useEffect(() => {

    // 화면이 리사이즈됨에 따라 밀리는 스크롤을 보정하여 유지시킨다.
    const handleResize = () => {
      const scrollContainer = carouselRef.current;
      const viewWidth = carouselRef.current?.getBoundingClientRect().width as number; // 현재 컨텐츠 컨테이너 창 넓이
      const columnGapOffset = viewWidth*(0.08); // 0.08은 column-gap만큼의 offset

      if (scrollContainer) {
        scrollContainer.scrollLeft = (viewWidth + columnGapOffset) * (carouselIdx);
      }
    };

    const debouncedUpdateNewBreakPoints = _.debounce(() => {
      const newBreakPoints = updateCarouselBreakPoints();
      if(newBreakPoints){
        carouselBreakPointsRef.current = newBreakPoints;
        console.log("BreakPoints를 업데이트 했습니다.", newBreakPoints);
        // setCarouselIdx(carouselIdx);
      }
    }, 500);


    window.addEventListener('resize', handleResize);
    window.addEventListener('resize', debouncedUpdateNewBreakPoints);

    return () => {
      window.removeEventListener('resize', handleResize)
      debouncedUpdateNewBreakPoints.cancel();
      window.removeEventListener('resize', debouncedUpdateNewBreakPoints);
    };
  }, [carouselIdx]);



  return (
    <>
      <div className='absolute right-3 -top-7 text-xl'>{`(${carouselIdx + 1}/${carouselBreakPointsRef.current?.length})`}</div>
      <div className='border-2 border-gray-300 p-3'>
        <div ref={carouselRef} style={{ overflowX: 'hidden' }}>
          {children}
        </div>
      </div>
    </>
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
  

  function areArraysEqual(arr1 : number[], arr2 : number[]) : boolean {
    // 먼저 배열 길이 비교
    if (arr1.length !== arr2.length) {
      return false;
    }
  
    // 배열의 각 요소 비교
    for (let i = 0; i < arr1.length; i++) {
      if (arr1[i] !== arr2[i]) {
        return false;
      }
    }
  
    // 모든 요소가 동일하면 true 반환
    return true;
  }
  
};

export default Carousel;
