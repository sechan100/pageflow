/* eslint-disable react-hooks/exhaustive-deps */
import { useEffect } from "react";
import { Outline } from "../../types/types";
import Navbar from "../nav/Navbar";
import PageCursor, { useLocationStore } from "../nav/PageCursor";
import ViewerContext from "./ViewerContext";
import { useGetPage } from "../api/page-api";





export default function Viewer({outline} : {outline: Outline}) {

  const locationStore = useLocationStore();

  useEffect(() => {
    locationStore.setTotalChapters(outline.chapters.length);
    locationStore.setChapterVolumes(outline.chapters); // 전체 chapter 별로, page의 length를 저장  ex)[1, 2, 5, 7, 3,]
  }, [outline]);

  return (
    <>
      <PageCursor />
      <Navbar outline={outline} />
      <ViewerContext outline={outline} />
    </>
  );

}