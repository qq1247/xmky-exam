package com.wcpdoc.exam.exam.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.annotation.Resource;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.wcpdoc.exam.core.dao.BaseDao;
import com.wcpdoc.exam.core.entity.LoginUser;
import com.wcpdoc.exam.core.service.impl.BaseServiceImp;
import com.wcpdoc.exam.core.util.ValidateUtil;
import com.wcpdoc.exam.exam.dao.QuestionDao;
import com.wcpdoc.exam.exam.entity.Question;
import com.wcpdoc.exam.exam.entity.QuestionType;
import com.wcpdoc.exam.exam.service.QuestionService;
import com.wcpdoc.exam.exam.service.QuestionTypeService;
import com.wcpdoc.exam.file.entity.FileEx;
import com.wcpdoc.exam.file.service.FileService;
import com.wcpdoc.exam.sys.entity.Org;
import com.wcpdoc.exam.sys.entity.Post;
import com.wcpdoc.exam.sys.service.UserService;

/**
 * 试题服务层实现
 * 
 * v1.0 zhanghc 2017-05-07 14:56:29
 */
@Service
public class QuestionServiceImpl extends BaseServiceImp<Question> implements QuestionService {
	@Resource
	private QuestionDao questionDao;
	@Resource
	private QuestionTypeService questionTypeService;
	@Resource
	private UserService userService;
	@Resource
	private FileService fileService;

	@Override
	@Resource(name = "questionDaoImpl")
	public void setDao(BaseDao<Question> dao) {
		super.dao = dao;
	}

	@Override
	public List<Map<String, Object>> getQuestionTypeTreeList(Integer  userId) {
		Org org = userService.getOrg(userId);
		List<Post> postList = userService.getPostList(userId);
		List<QuestionType> questionTypeList = questionTypeService.getList();
		List<Map<String, Object>> questionTypeTreeList = new ArrayList<Map<String,Object>>();
		
		for(QuestionType questionType : questionTypeList){
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("ID", questionType.getId());
			map.put("NAME", questionType.getName());
			map.put("PARENT_ID", questionType.getParentId());
			//map.put("DISABLED", true);
			//map.put("EXPANDED", true);
			
			if(userId == 1){
				questionTypeTreeList.add(map);
				continue;
			}
			
			if(questionType.getUserIds() != null 
					&& questionType.getUserIds().contains(userId.toString())){//有用户权限
				questionTypeTreeList.add(map);
				continue;
			}
			if(questionType.getOrgIds() != null 
					&& questionType.getOrgIds().contains(org.getId().toString())){//有机构权限
				questionTypeTreeList.add(map);
				continue;
			}
			
			for(Post post : postList){
				if(questionType.getPostIds() != null 
						&& questionType.getPostIds().contains(post.getId().toString())){//有岗位权限
					questionTypeTreeList.add(map);
					break;
				}
			}
		}
		
		return questionTypeTreeList;
	}

	@Override
	public QuestionType getQuestionType(Integer id) {
		return questionDao.getQuestionType(id);
	}

	@Override
	public void doQuestionTypeUpdate(Integer[] ids, Integer questionTypeId) {
		//校验数据有效性
		if(!ValidateUtil.isValid(ids)){
			throw new RuntimeException("无法获取参数：ids");
		}
		if(questionTypeId == null){
			throw new RuntimeException("无法获取参数：questionTypeId");
		}
		
		//完成设置试题
		for(Integer id : ids){
			Question question = questionDao.getEntity(id);
			if(question.getQuestionTypeId() == questionTypeId){
				continue;
			}
			
			question.setQuestionTypeId(questionTypeId);
			questionDao.update(question);
		}
	}

	@Override
	public List<Question> getList(Integer questionTypeId) {
		return questionDao.getList(questionTypeId);
	}

	@Override
	public void delAndUpdate(Integer[] ids) {
		//校验数据有效性
		if(!ValidateUtil.isValid(ids)){
			throw new RuntimeException("无法获取参数：ids");
		}
		
		//删除试题
		for(Integer id : ids){
			Question question = getEntity(id);
			question.setState(0);
			update(question);
		}
	}

	@Override
	public QuestionType getQuestionType2(Integer questionTypeId) {
		return questionTypeService.getEntity(questionTypeId);
	}

	@Override
	public String doTempUpload(MultipartFile[] files, String[] allowTypes, LoginUser user, String ip) {
		return fileService.doTempUpload(files, allowTypes, user, ip);
	}

	@Override
	public FileEx getFileEx(Integer fileId) {
		return fileService.getEntityEx(fileId);
	}

	@Override
	public void saveAndUpdate(Question question, String[] answer, LoginUser user, String ip) {
		//保存试题
		if(question.getType() == 3){//如果是填空，特殊处理一下
			StringBuilder answers = new StringBuilder();
			for(String an : answer){
				if(answers.length() > 0){
					answers.append("\n");
				}
				answers.append(an);
			}
			question.setAnswer(answers.toString());
		}
		question.setUpdateTime(new Date());
		question.setUpdateUserId(user.getId());
		save(question);
		
		//保存附件
		try {
			saveFile(question, user, ip);
		} catch (Exception e) {
			try {
				throw e;
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}

	private void saveFile(Question question, LoginUser user, String ip) throws Exception {
		List<Integer> fileIdList = html2FileIds(question.getTitle());
		
		if(question.getType() == 1 || question.getType() == 2) {//单选或多选
			if(ValidateUtil.isValid(question.getOptionA())){
				fileIdList.addAll(html2FileIds(question.getOptionA()));
			}
			if(ValidateUtil.isValid(question.getOptionB())){
				fileIdList.addAll(html2FileIds(question.getOptionB()));
			}
			if(ValidateUtil.isValid(question.getOptionC())){
				fileIdList.addAll(html2FileIds(question.getOptionC()));
			}
			if(ValidateUtil.isValid(question.getOptionD())){
				fileIdList.addAll(html2FileIds(question.getOptionD()));
			}
			if(ValidateUtil.isValid(question.getOptionE())){
				fileIdList.addAll(html2FileIds(question.getOptionE()));
			}
			if(ValidateUtil.isValid(question.getOptionF())){
				fileIdList.addAll(html2FileIds(question.getOptionF()));
			}
			if(ValidateUtil.isValid(question.getOptionG())){
				fileIdList.addAll(html2FileIds(question.getOptionG()));
			}
		}else if(question.getType() == 5){//问答
			fileIdList.addAll(html2FileIds(question.getAnswer()));
		}
		
		fileIdList.addAll(html2FileIds(question.getAnalysis()));
		
		for(Integer fileId : fileIdList){
			fileService.doUpload(fileId, user, ip);
		}
	}

	private List<Integer> html2FileIds(String html) {
		List<Integer> fileIdList = new ArrayList<>();
		if(!ValidateUtil.isValid(html)){
			return fileIdList;
		}
		
		Document document = Jsoup.parse(html);
		Elements embeds = document.getElementsByTag("embed");
		ListIterator<Element> listIterator = embeds.listIterator();
		while(listIterator.hasNext()){
			Element next = listIterator.next();
			String url = next.attr("flashvars");
			String[] params = url.split("\\?")[1].split("&");;
			for(String param : params){
				String[] kv = param.split("=");
				if(kv[0].equals("fileId")){
					fileIdList.add(Integer.parseInt(kv[1]));
				}
			}
		}
		
		Elements imgs = document.getElementsByTag("img");
		listIterator = imgs.listIterator();
		while(listIterator.hasNext()){
			Element next = listIterator.next();
			String url = next.attr("src");
			if(url.startsWith("data")){
				continue;//字符串式的图片
			}
			
			String[] params = url.split("\\?")[1].split("&");;
			for(String param : params){
				String[] kv = param.split("=");
				if(kv[0].equals("fileId")){
					fileIdList.add(Integer.parseInt(kv[1]));
				}
			}
		}
		return fileIdList;
	}

	@Override
	public void updateAndUpdate(Question question, String[] answer, LoginUser user, String ip) {
		//修改试题
		Question entity = getEntity(question.getId());
		if(entity.getType() == 3){//如果是填空，特殊处理一下
			StringBuilder answers = new StringBuilder();
			for(String an : answer){
				if(answers.length() > 0){
					answers.append("\n");
				}
				answers.append(an);
			}
			question.setAnswer(answers.toString());
		}
		
//		entity.setType(question.getType());//不允许修改类型
		entity.setState(question.getState());
		entity.setDifficulty(question.getDifficulty());
		entity.setTitle(question.getTitle());
		entity.setOptionA(question.getOptionA());
		entity.setOptionB(question.getOptionB());
		entity.setOptionC(question.getOptionC());
		entity.setOptionD(question.getOptionD());
		entity.setOptionE(question.getOptionE());
		entity.setOptionF(question.getOptionF());
		entity.setOptionG(question.getOptionG());
		entity.setAnswer(question.getAnswer());
		entity.setAnalysis(question.getAnalysis());
		entity.setUpdateTime(new Date());
		entity.setUpdateUserId(user.getId());
		update(entity);
		
		//修改附件
		try {
			saveFile(entity, user, ip);
		} catch (Exception e) {
			try {
				throw e;
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}
}
