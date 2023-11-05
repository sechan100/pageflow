

export interface IBook {
  author: any,
  chapters: IChapter[],
  coverImgUrl: string,
  createDate: string,
  id: number,
  modifyDate: string,
  published: boolean
  title: string,
}


export interface IChapter {
  createDate: string,
  id: number,
  modifyDate: string,
  orderNum: number,
  pages: IPage[] | null,
  title: string,
}

export interface IPage {
  "id": number,
  "title": string,
  "content": string
}


