import he from 'he'

const escapeString = (str: string): string => {
	if (!str) return '' 
    return he.decode(str)
};
export const escape2Html = <T extends string | string[]>(txt: T): T => {
    if (Array.isArray(txt)) {
        return (txt as string[]).map((_txt) => escapeString(_txt)) as T;
    } else {
        return escapeString(txt as string) as T;
    }
}
