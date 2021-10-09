package com.wcpdoc.exam.base.service.impl;

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

import com.wcpdoc.exam.base.entity.Parm;
import com.wcpdoc.exam.base.service.ParmExService;
import com.wcpdoc.exam.core.dao.BaseDao;
import com.wcpdoc.exam.core.exception.MyException;
import com.wcpdoc.exam.core.service.impl.BaseServiceImp;
import com.wcpdoc.exam.file.entity.FileEx;
import com.wcpdoc.exam.file.service.FileService;
import com.wcpdoc.exam.notify.exception.EmailException;
import com.wcpdoc.exam.notify.service.EmailService;

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
	    String filepath = String.format(".\\config\\favicon.ico");
	    try {
	      ImageIO.write(bufferedImage, "png",  new File(filepath));
	      //System.out.println("文件已经生成，路经为" + filepath);
	    }
	    catch (IOException ex) {
	      ex.printStackTrace();
	    }
	}
}
