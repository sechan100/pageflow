/* eslint-disable @typescript-eslint/no-unused-vars */


export interface IOutlineSidebarProps {
  children: React.ReactNode;
  bookId: number;
  queryClient: any;
}


export default function OutlineSidebarWrapper(drillingProps : IOutlineSidebarProps) {


  return (
    <>
      <aside id="page-outline-sidebar" className="fixed top-0 left-0 z-40 w-64 h-screen transition-transform -translate-x-full sm:translate-x-0">
        <div className="overflow-y-auto pt-12 pb-5 px-3 h-full bg-white border-r border-gray-200 dark:bg-gray-800 dark:border-gray-700">
          {drillingProps.children}
        </div>
      </aside>
      <AddChapterBtn {...drillingProps} />
      <div id="sidebar-placeholder" className="relative w-64 h-screen transition-transform -translate-x-full sm:translate-x-0"></div>
    </>
  );
}



interface IAddChapterBtnProps {
  bookId: number;
  queryClient: any;
}


function AddChapterBtn(drillingProps : IAddChapterBtnProps) {

  function addChapter() {
    const newChapter = {
      id: Math.floor(Math.random() * 1000000000),
      title: "새 챕터",
      pages: []
    };
  }


  return (
    <button type="button" onClick={addChapter} className="fixed z-40 top-4 left-40 px-5 py-2 text-xs font-medium text-center ext-gray-900 focus:outline-none bg-white rounded-lg border border-gray-200 hover:bg-gray-100 hover:text-blue-700 dark:focus:ring-gray-700 dark:bg-gray-800 dark:text-gray-400 dark:border-gray-600 dark:hover:text-white dark:hover:bg-gray-700">새 챕터</button>
  );
}