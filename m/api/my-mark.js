import request from '@/request/request';

export function myMarkPaper(parm) {
	return request.post('/myMark/paper', parm);
}
