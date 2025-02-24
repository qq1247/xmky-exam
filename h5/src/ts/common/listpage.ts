export interface Listpage {
    curPage: number;
    pageSize: number;
    total: number;
    list: any[];
    [key: string]: any; // 扩展字段

}
