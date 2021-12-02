package com.wcpdoc.base.service.impl;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.springframework.stereotype.Service;

import com.wcpdoc.base.entity.Parm;
import com.wcpdoc.base.service.ParmExService;
import com.wcpdoc.core.dao.BaseDao;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.service.impl.BaseServiceImp;
import com.wcpdoc.file.entity.FileEx;
import com.wcpdoc.file.service.FileService;
import com.wcpdoc.notify.exception.EmailException;
import com.wcpdoc.notify.service.EmailService;

/**
 * 参数扩展服务层实现
 * 
 * v1.0 zhanghc 2016-6-11下午8:57:40
 */
@Service
public class ParmExServiceImpl extends BaseServiceImp<Parm> implements ParmExService {
	@Resource
	private EmailService emailService;
	@Resource
	private FileService fileService;
	
	@Override
	public void addAndUpdate(Parm parm) {
		try {
			emailService.init();
		} catch (EmailException e) {
			throw new MyException(e.getMessage());
		}
	}

	@Override
	public void updateAndUpdate(Parm parm) {
		try {
			emailService.init();
		} catch (EmailException e) {
			throw new MyException(e.getMessage());
		}
	}

	@Override
	public void setDao(BaseDao<Parm> dao) {
		
	}

	@Override
	public void ImageIcon(Parm parm) throws Exception{
		if (parm.getOrgLogo() == null) {
			return;// 如果logo为空，不处理
		}
		FileEx fileEx = fileService.getFileEx(parm.getOrgLogo());
	    FileInputStream in = new FileInputStream(fileEx.getFile());
	    byte[] bytes = new byte[in.available()];
	    in.read(bytes);
	    in.close();
	    ImageIcon imageIcon = new ImageIcon(bytes);
	    BufferedImage bufferedImage=new BufferedImage(64,64,BufferedImage.TYPE_INT_RGB);
	    Graphics2D g=(Graphics2D)bufferedImage.getGraphics();
	    g.setColor(Color.blue);
	    g.drawRect(5,5,5,5);
	    g.fillRect(5,5,5,5);
	    g.drawImage(imageIcon.getImage(),0,0,64,64,imageIcon.getImageObserver());
	    String filepath = String.format("./config/favicon.ico");
	    try {
	      ImageIO.write(bufferedImage, "png",  new File(filepath));
	      //System.out.println("文件已经生成，路经为" + filepath);
	    }
	    catch (IOException ex) {
	      ex.printStackTrace();
	    }
	}
	
	@Override
	public void doUpload(Parm parm) {
		fileService.doUpload(parm.getOrgLogo());
	}
}
