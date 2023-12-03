/* eslint-disable react-hooks/exhaustive-deps */
import { useEffect } from "react";
import { Outline } from "../../types/types";
import Navbar from "../nav/Navbar";
import PageCursor, { useLocationStore } from "../nav/PageCursor";
import ViewerContext from "./ViewerContext";






export default function Viewer({outline} : {outline: Outline}) {

  const locationStore = useLocationStore();

  useEffect(() => {
    locationStore.setTotalChapters(outline.chapters.length);
    locationStore.setChapterVolumes(outline.chapters); // 전체 chapter 별로, page의 length를 저장  ex)[1, 2, 5, 7, 3,]
  }, [outline]);

  useEffect(() => {
    const currentLocation = JSON.stringify(locationStore.location);
    localStorage.setItem('lastLocation', currentLocation);
    console.log('마지막 위치', currentLocation); // 마지막 위치가 chapterIdx, pageIdx로 찍힘 캐러셀의 breakPoint로 변경 필요
  }, [locationStore.location]);

  return (
    <>
      <PageCursor />
      <Navbar outline={outline} />
      <ViewerContext outline={outline} />
    </>
  );

}