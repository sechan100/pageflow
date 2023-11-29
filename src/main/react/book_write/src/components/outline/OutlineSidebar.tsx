import { Outline } from "../../types/types";
import NewChapterBtn from "./newItemBtn/NewChapterBtn";
import NewPageBtn from "./newItemBtn/NewPageBtn";


export interface IOutlineSidebarProps {
  children: React.ReactNode;
  outline: Outline;
}


export default function OutlineSidebar({children, outline: localOutline} : IOutlineSidebarProps) {

  // 사이드바 비율
  const sidebarRatio = 20;

  return (
    <>
      <div className={`flex w-[${sidebarRatio}vw]`}>
        <aside id="page-outline-sidebar-placeholder" className="z-50 h-screen w-full transition-transform -translate-x-full sm:translate-x-0"></aside>
      </div>
      <aside id="page-outline-sidebar" className={`fixed z-50 h-screen w-[${sidebarRatio}vw] transition-transform -translate-x-full sm:translate-x-0`}>
        <div className="invisible-scroll overflow-y-auto relative pt-16 pb-5 px-3 h-full bg-white border-r border-gray-200 dark:bg-gray-800 dark:border-gray-700">
          {children}
          <NewChapterBtn outline={localOutline} />
          <NewPageBtn outline={localOutline} />
        </div>
      </aside>
    </>
  );
}
