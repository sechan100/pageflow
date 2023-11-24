export interface Outline {
  id: number,
  author: Author,
  title: string,
  coverImgUrl: string,
  published: boolean
  chapters: ChapterSummary[],
}


export interface ChapterSummary {
  id: number,
  title: string,
  sortPriority: number,
  pages: PageSummary[],
}

export interface PageSummary {
  id: number,
  title: string,
  sortPriority : number
}

export interface IPage {
  id: number,
  title: string,
  content: string
}


export interface Author {
  id: number,
  createDate: string,
  modifyDate: string,
  nickname: string,
  profileImgUrl: string,
}


export interface OutlineMutation {
  chapters: ChapterSummary[] | null
}


export interface BookMutation {
  title: string | null,
  coverImg: File | null,
}


export interface ChapterMutation {
  id: number | null,
  title: string | null,
}


export interface PageMutation {
  id: number | null,
  title: string | null,
  content: string | null,
}