import React from 'react';
import './App.css';
import Viewer from './components/viewer/Viewer';
import { create } from 'zustand';
import { Outline, IPage } from './types/types';
import axios from 'axios';
import { useState, useEffect } from 'react';
import { QueryClient, QueryClientProvider } from 'react-query';


// const dummyOutline =         
// {
//   id: 1,
//   author: {
//     id: 1,
//     createDate: "생성일",
//     modifyDate: "수정일",
//     nickname: "닉네임",
//     profileImgUrl: "https://phinf.pstatic.net/contact/20230727_252/1690456995185MmBBn_JPEG/image.jpg"
//     },
//   title: "책이 로딩중입니다...",
//   published: false,
//   coverImgUrl: "/img/unloaded_img.jpg",
//   chapters: [
//     {
//       id: 1, 
//       title: "챕터1",
//       sortPriority: 10000,
//       pages: [
//         {
//           id: 1,
//           title: "페이지1",
//           sortPriority: 10000
//         },
//         {
//           id: 2,
//           title: "페이지2",
//           sortPriority: 20000
//         },
//         {
//           id: 3,
//           title: "페이지3",
//           sortPriority: 30000
//         },
//         {
//           id: 4,
//           title: "페이지4",
//           sortPriority: 40000
//         },
//         {
//           id: 5,
//           title: "페이지5",
//           sortPriority: 50000
//         }
//       ]
//     },
//     {
//       id: 2,
//       title: "챕터2",
//       sortPriority: 20000,
//       pages: [
//         {
//           id: 6,
//           title: "페이지6",
//           sortPriority: 60000
//         },
//         {
//           id: 7,
//           title: "페이지7",
//           sortPriority: 70000
//         },
//         {
//           id: 8,
//           title: "페이지8",
//           sortPriority: 80000
//         },
//         {
//           id: 9,
//           title: "페이지9",
//           sortPriority: 90000
//         },
//       ]
//     },
//     {
//       id: 3,
//       title: "챕터3",
//       sortPriority: 30000,
//       pages: [
//         {
//           id: 10,
//           title: "페이지10",
//           sortPriority: 100000
//         },
//         {
//           id: 11,
//           title: "페이지11",
//           sortPriority: 11000
//         },
//         {
//           id: 12,
//           title: "페이지12",
//           sortPriority: 12000
//         },
//         {
//           id: 13,
//           title: "페이지13",
//           sortPriority: 130000
//         },
//         {
//           id: 14,
//           title: "페이지14",
//           sortPriority: 140000
//         },
//         {
//           id: 15,
//           title: "페이지15",
//           sortPriority: 150000
//         },
//       ]
//     }
//   ]
// }



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
  }, [outline])


  if (isLoading) {
    return <div>Loading...</div>;
  }

  if (!outline) {
    return <div>No data available.</div>;
  }


  const queryClient = new QueryClient({
    defaultOptions: {
      queries: {
        refetchOnMount: false,
        refetchOnWindowFocus: false,
        retry: false,
        staleTime: 1000 * 60 * 60 // 1시간
      }
    }
  });

  return (
    <QueryClientProvider client={queryClient}>
      <Viewer outline={outline}></Viewer>
    </QueryClientProvider>
  );
}

export default App;
