import React from 'react';
import './App.css';
import Viewer from './components/viewer/Viewer';
import { create } from 'zustand';
import { Outline, IPage } from './types/types';
import axios from 'axios';
import { useState, useEffect } from 'react';
import { QueryClient, QueryClientProvider } from 'react-query';
import { useLocationStore } from './components/nav/PageCursor';




function App() {
  const [outline, setOutline] = useState<Outline | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    // @ts-ignore
    const bookId = window.APP_BOOK_ID;

    setIsLoading(true);

    // Outline 데이터 요청
    axios.get(`/api/books/${bookId}/outline`)
    .then((response) => {
      if(response){
        setOutline(response.data);
        console.log("### outline data fetching success! ###", response.data);
        return response.data;
      }
    })
    .catch((error) => {
      console.log(error);
    })
  }, []);

  useEffect(() => {
    if(outline){
      setIsLoading(false);
    }
    // 마지막 위치 복원
    const savedLocation = localStorage.getItem('lastLocation');
    if (savedLocation && outline) {
      const { chapterIdx, pageIdx } = JSON.parse(savedLocation);
      // 여기서 마지막 위치 정보를 'Viewer' 컴포넌트에 전달하는 로직을 추가하거나,
      // 다른 방식으로 해당 위치로 이동하도록 구현합니다.
      const locationStore = useLocationStore.getState();
      locationStore.setLocation({ chapterIdx, pageIdx });
    }
  }, [outline])


  if (isLoading) {
    return <div>Loading...</div>;
  }

  if (!outline) {
    return <div>No data available.</div>;
  }


  const queryClient = new QueryClient({ 
    // React Query 라이브러리의 QueryClient를 초기화하고 구성하는 부분
    // QueryClient는 React Query에서 데이터 페칭, 캐싱, 상태 관리 등을 수행
    // 애플리케이션에서 데이터를 요청하고 관리할 수 있다.

    defaultOptions: {
      queries: {
        refetchOnMount: false, // 기본값 true, 쿼리가 컴포넌트에 마운트될 때 자동으로 데이터를 다시 가져오는지 여부 결정
        refetchOnWindowFocus: false, // 기본값 true, 브라우저 창이나 탭이 다시 포커스 될 때 데이터를 자동으로 다시 가져올지 여부를 결정
        retry: false, //데이터 페칭이 실패했을 때, 자동으로 재시도 횟수를 정한다.
        staleTime: 1000 * 60 * 60 // 1시간
      }
    }
  });

    // 이어보기 버튼 클릭 핸들러
    // 해당 버튼은 서버의 detail 페이지에 구현 필요
    const handleContinueReading = () => {
      const savedLocation = localStorage.getItem('lastLocation');
      if (savedLocation) {
        const { chapterIdx, pageIdx } = JSON.parse(savedLocation);
        const locationStore = useLocationStore.getState();
        locationStore.setLocation({ chapterIdx, pageIdx });
      }
    };

  return (
    <QueryClientProvider client={queryClient}>
      <Viewer outline={outline}></Viewer>
    </QueryClientProvider>
  ); 
  // QueryClientProvider 내부에 위치한 컴포넌트들은 useQuery, useMutation 등의 훅을 사용해 서버로부터 데이터 요청, 업데이트
  // queryClient 인스턴스가 제공하는 설정을 기반으로 동작.
  // Viewer 내부에서 데이터를 페칭하거나 캐시된 데이터를 사용하는 로직 구현 가능.
}

export default App;
