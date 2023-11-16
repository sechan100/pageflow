export interface Outline {
  id: number,
  author: Author,
  title: string,
  coverImgUrl: string,
  published: boolean
  chapters: ChapterSummary[] | null,
}


export interface ChapterSummary {
  id: number,
  title: string,
  sortPriority: number,
  pages: PageSummary[] | null,
}

export interface PageSummary {
  id: number,
  title: string,
  sortPriority : number
}


export interface Author {
  id: number,
  createDate: string,
  modifyDate: string,
  nickname: string,
  profileImgUrl: string,
}



export interface BookUpdateRequest {
  title: string,
  coverImg: File | null,
}