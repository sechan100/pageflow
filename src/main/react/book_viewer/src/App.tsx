import React from 'react';
import './App.css';
import Navbar from './components/nav/Navbar';
import Viewer from './components/viewer/Viewer';


const dummyOutline =         
{
  id: 1,
  author: {
    id: 1,
    createDate: "생성일",
    modifyDate: "수정일",
    nickname: "닉네임",
    profileImgUrl: "https://phinf.pstatic.net/contact/20230727_252/1690456995185MmBBn_JPEG/image.jpg"
    },
  title: "책이 로딩중입니다...",
  published: false,
  coverImgUrl: "/img/unloaded_img.jpg",
  chapters: [
    {
      id: 1, 
      title: "챕터1",
      sortPriority: 10000,
      pages: [
        {
          id: 1,
          title: "페이지1",
          sortPriority: 10000
        },
        {
          id: 2,
          title: "페이지2",
          sortPriority: 20000
        },
        {
          id: 3,
          title: "페이지3",
          sortPriority: 30000
        },
        {
          id: 4,
          title: "페이지4",
          sortPriority: 40000
        },
        {
          id: 5,
          title: "페이지5",
          sortPriority: 50000
        }
      ]
    },
    {
      id: 2,
      title: "챕터2",
      sortPriority: 20000,
      pages: [
        {
          id: 4,
          title: "페이지4",
          sortPriority: 10000
        },
        {
          id: 5,
          title: "페이지5",
          sortPriority: 20000
        },
        {
          id: 6,
          title: "페이지6",
          sortPriority: 60000
        },
        {
          id: 7,
          title: "페이지7",
          sortPriority: 70000
        },
        {
          id: 8,
          title: "페이지8",
          sortPriority: 80000
        },
        {
          id: 9,
          title: "페이지9",
          sortPriority: 90000
        },
      ]
    },
    {
      id: 3,
      title: "챕터3",
      sortPriority: 30000,
      pages: [
        {
          id: 10,
          title: "페이지10",
          sortPriority: 100000
        },
        {
          id: 11,
          title: "페이지11",
          sortPriority: 11000
        },
        {
          id: 12,
          title: "페이지12",
          sortPriority: 12000
        },
        {
          id: 13,
          title: "페이지13",
          sortPriority: 130000
        },
        {
          id: 14,
          title: "페이지14",
          sortPriority: 140000
        },
        {
          id: 15,
          title: "페이지15",
          sortPriority: 150000
        },
      ]
    }
  ]
}


function App() {
  return (
    <Viewer outline={dummyOutline}></Viewer>
  );
}

export default App;
