package PaDialogs;


import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import PaAlgorithms.PaAlgoTransform;
import PaCollection.PaAlbum;
import PaCollection.PaImage;
import PaCollection.PaSubject;
import PaExif.PaExtendedExf;
import PaExif.PaImageExf;
import PaExif.PaExifLoader;
import PaForms.PaSubjectsListModel;
import PaGlobal.PaGuiTools;
import PaGlobal.PaUtils;
import PaImage.PaViewPhotosForm;
import static PaGlobal.PaLog.writeLog;
import static PaGlobal.PaUtils.*;
import static PaExif.PaTagParser.*;

public class PaImagePropertiesDialog extends JDialog {
	
	private static final long serialVersionUID = 1L;
	
	JScrollPane m_tableScrollPane = null;
	
	JTable m_exifTable = null;
	
	private PaImage _photo;
	
	int m_albumId;
	
	public PaImagePropertiesDialog (JFrame jfrm, String nameFrame, PaImage photo, int albumId) {
		super (jfrm, getGuiStrs("propertiesCaptionPropertiesDialog"), true); 
		
		_photo = photo;
		
		m_albumId = albumId;

		addWindowListener(new WindowAdapter() {
			
			public void windowClosing(WindowEvent we) {
				
				dispose();
			}
		});
		
		add(createGUI());
		
		setBounds(250, 150, 225, 220);
		
		m_tableScrollPane.setPreferredSize(new Dimension(250,300));

		pack();
		
		setResizable(false);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private JPanel createGUI () {
		
		JLabel namePhoto = new JLabel(getGuiStrs("nameImagePropertiesDialogCaption"));

		JLabel namePhotoData = new JLabel("   " + _photo.getName());
		
		JPanel panelName = PaGuiTools.createHorizontalPanel();
		
		panelName.add(namePhoto);  panelName.add(namePhotoData);  panelName.add(Box.createHorizontalGlue());
		
		JLabel nameAlbum = new JLabel(getGuiStrs("albumNameImagePropertiesDialogCaption")); 
		
		String alname = " ";
		
		PaAlbum al = null;
		
		if(m_albumId != -1) {
			
			al = PaUtils.get().getAlbumContainer().getAlbum(m_albumId);
		}
		else {
			
			int id = _photo.getAuxiliaryAlbumId();
			
			if(id != -1) {
				
				al = PaUtils.get().getAlbumContainer().getAlbum(id);
			}
		}
		
		if(al != null) {
			
			alname = al.getName();
		}
		
		JLabel nameAlbumData = new JLabel("   " + alname);
		
		JPanel panelAlbum = PaGuiTools.createHorizontalPanel();
		
		panelAlbum.add(nameAlbum);  panelAlbum.add(nameAlbumData);  panelAlbum.add(Box.createHorizontalGlue());
		
		JLabel datePhoto = new JLabel(getGuiStrs("dateImagePropertiesDialog")); 

		JLabel datePhotoData = new JLabel("   " + dateToString(_photo.getDate(),GUI_DATE_FORMAT)); 

		JPanel panelDate = PaGuiTools.createHorizontalPanel();
		
		panelDate.add(datePhoto);  panelDate.add(datePhotoData);  panelDate.add(Box.createHorizontalGlue());
		
		JLabel locationPhoto = new JLabel(getGuiStrs("imageFilePropertiesDialog")); 

		JLabel locationPhotoData = new JLabel("   " + _photo.getFullPath()); 

		JPanel panelLocation = PaGuiTools.createHorizontalPanel();
		
		panelLocation.add(locationPhoto);  panelLocation.add(locationPhotoData);  panelLocation.add(Box.createHorizontalGlue());
		
		JLabel creationDate = new JLabel(getGuiStrs("fileCreationDatePropertiesDialog"));
		
		JLabel creationDateData = new JLabel(); 
		
		JPanel panelCrDate = PaGuiTools.createHorizontalPanel();
		
		panelCrDate.add(creationDate);  panelCrDate.add(creationDateData);  panelCrDate.add(Box.createHorizontalGlue());
		 
		JLabel modificationDate = new JLabel(getGuiStrs("fileModificationDatePropertiesDialog"));
		
		JLabel modificationDateData = new JLabel();
		
		JPanel panelMdDate = PaGuiTools.createHorizontalPanel();
		
		panelMdDate.add(modificationDate);  panelMdDate.add(modificationDateData); panelMdDate.add(Box.createHorizontalGlue());
		  
		JLabel valueFilePhoto = new JLabel(getGuiStrs("fileSizePropertiesDialog"));
		
		JLabel valueFilePhotoData = new JLabel();
		
		JPanel panelVFile = PaGuiTools.createHorizontalPanel();
		
		panelVFile.add(valueFilePhoto);  panelVFile.add(valueFilePhotoData); panelVFile.add(Box.createHorizontalGlue());
			
		JLabel commentLabel = new JLabel(getGuiStrs("commentLabelPropertiesDialog"));
		
		JLabel commentLabelData = new JLabel("   " + _photo.getComments());
		
		JPanel panelComment = PaGuiTools.createHorizontalPanel();
		
		panelComment.add(commentLabel);  panelComment.add(commentLabelData); panelComment.add(Box.createHorizontalGlue());
		
		 
		 PaGuiTools.makeSameSize(new JComponent[] { namePhoto,datePhoto,locationPhoto,
				 creationDate, modificationDate, valueFilePhoto, commentLabel, nameAlbum });
		 
		 
	    File f = new File(_photo.getFullPath());
	    
	    if ( f.exists() ) {
	    	
	    long len = f.length();
	      
		    try {
		    	
				BasicFileAttributes attrs = Files.readAttributes(f.toPath(), BasicFileAttributes.class);
				
	
	            SimpleDateFormat ftFormatter = new SimpleDateFormat("yyyy-MM-dd");
	            
	            SimpleDateFormat formatter = new SimpleDateFormat(GUI_DATE_FORMAT);
				
				creationDateData.setText("   " + formatter.format( ftFormatter.parse(attrs.creationTime().toString()) ) );
				
				modificationDateData.setText("   " + 
				formatter.format( ftFormatter.parse(attrs.lastModifiedTime().toString()) ) );
				
			} catch (IOException | ParseException e) {
				
				writeLog("IOException | ParseException :  " + NEXT_ROW, e, true, false, true);
			}
		    
		    valueFilePhotoData.setText("   " + len + "  " + getGuiStrs("byteLabelCaption"));
	    }

		JList<?> subjectView;
		
		PaSubjectsListModel listModel = new PaSubjectsListModel();
		
		for (int sub : _photo.getSubjectsList()) {
			
			for (PaSubject subject : PaUtils.get().getSubjectsContainer().get_tems()) {
				
				if (sub == subject.getId()) {
					
					listModel.addTema(subject);
				}
				
			}
		}

		JPanel panel_MAIN = PaGuiTools.createVerticalPanel();

		panel_MAIN.setBorder( BorderFactory.createEmptyBorder(12,12,12,12));
		
		//list of subjects
		subjectView = new JList(listModel);
		
		subjectView.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		JScrollPane scrol = new JScrollPane(subjectView);
		
		scrol.setViewportBorder(BorderFactory.createLoweredBevelBorder());
		
		scrol.setBorder(BorderFactory.createEmptyBorder());
		
		subjectView.setMinimumSize(new Dimension(200,100));
		
		int scale = 120;
		
		PaViewPhotosForm viewForm = new PaViewPhotosForm(_photo, scale);
		
		viewForm.setBackground(null);
		
		JPanel panelImage = PaGuiTools.createHorizontalPanel();
		
		panelImage.add(viewForm);
		
		panelImage.add(scrol);
		
		panelImage.add(Box.createHorizontalGlue());
		
		panel_MAIN.add(panelImage);
		
		Image icBoost = PaUtils.get().getBoostImageForId(viewForm.getImage(), PaUtils.get().getMainContainer().getCurrentContainer());
		
		ImageIcon icon = null;
		
		if(icBoost == null) {
			
			icon = new ImageIcon (PaAlgoTransform.getScaledImage(new ImageIcon(viewForm.getImage().getFullPath()).getImage(),
				viewForm.m_widthIcon, viewForm.m_heightIcon));
		}
		else {
			
			icon = new ImageIcon (PaAlgoTransform.getScaledImage(icBoost,viewForm.m_widthIcon, viewForm.m_heightIcon));
			
		}
		
		viewForm.getIconLabel().setIcon(icon);
		
		intiExifTable();
		
		panel_MAIN.add(Box.createVerticalStrut(12));
		
		panel_MAIN.add(panelName);
		
		panel_MAIN.add(panelAlbum);
		
		panel_MAIN.add(panelLocation);
		
		panel_MAIN.add(panelCrDate);
		
		panel_MAIN.add(panelMdDate);
		
		panel_MAIN.add(panelVFile);
		
		panel_MAIN.add(panelDate);
		
		panel_MAIN.add(panelComment);
		
		JLabel metaDataLabel = new JLabel(getGuiStrs("exifMetaDataLabel"));
		
		JPanel panelMeta = PaGuiTools.createHorizontalPanel();
		
		panelMeta.add( metaDataLabel );
		
		panelMeta.add(Box.createHorizontalGlue());
	
		panel_MAIN.add(panelMeta);
		
		panel_MAIN.add(m_tableScrollPane);
	
		Font font = PaUtils.get().getBaseFont();
		
		namePhoto.setFont(font);
		
		namePhotoData.setFont(font);
		
		nameAlbum.setFont(font);
		
		nameAlbumData.setFont(font);
		
		datePhoto.setFont(font); 
		
		datePhotoData.setFont(font);
		
		locationPhoto.setFont(font);
		
		locationPhotoData.setFont(font);
		
		creationDate.setFont(font);
		
		creationDateData.setFont(font); 
		
		modificationDate.setFont(font);
		
		modificationDateData.setFont(font); 
		
		valueFilePhoto.setFont(font);
		
		valueFilePhotoData.setFont(font);	
		
		commentLabel.setFont(font);
		
		commentLabelData.setFont(font);
		
		metaDataLabel.setFont(font);

		return panel_MAIN;
	}
	
	/**
	 * <p>Initiates table with exif data</p>
	 */
	private void intiExifTable()
	{
		String[] columnNames = {getGuiStrs("exifTableParameterColumn"),
				getGuiStrs("exifTableDataColumn")};
		
		
		PaExifLoader loader = new  PaExifLoader();

		if(loader.loadFile(_photo.getFullPath())) {
		
			m_exifTable = new JTable(getExifData(loader), columnNames);
			
			m_tableScrollPane = new JScrollPane(m_exifTable);
			
		}
		else {
			
			Object[][] data = {};
			
			m_exifTable = new JTable(data, columnNames);
			
			m_tableScrollPane = new JScrollPane(m_exifTable);
		}
	}
	
	/**
	 * <p>Loads the exif data and removes the exif tags which have no data</p>
	 * @param loader - loader class for reading exif info
	 * @return the pairs with name of the tag and the value of the exif tag
	 */
	private Object[][] getExifData(PaExifLoader loader) {
		

		Object[][] data = {
		{getGuiStrs("exifTableMakerRow"),getString(loader.getExifElement(PaImageExf.Make),"Make") },
		
		{getGuiStrs("exifTableCameraRow"), getString(loader.getExifElement(PaImageExf.Model),"CameraModel") },
		
		{getGuiStrs("exifTableSoftwareRow"),getString(loader.getExifElement(PaImageExf.Software),"Software") },
		
		{getGuiStrs("exifTableOrientRow"),  getOrientation(loader.getExifElement(PaImageExf.Orientation))},
		
		{getGuiStrs("exifTableCompressionRow"),getCompression(loader.getExifElement(PaImageExf.Compression)) },
		
		{getGuiStrs("exifTableDateTimeRow"),getString(loader.getExifElement(PaImageExf.DateTime),"Date/time") },
		
		{getGuiStrs("exifTableArtistRow"),getString(loader.getExifElement(PaImageExf.Artist),"Artist") },
		
		{getGuiStrs("exifTableResUnitRow"),getResUnit(loader.getExifElement(PaImageExf.ResUnit)) },
		
		{getGuiStrs("exifTableBitsPerSampleRow"),getBitsPerSample(loader.getExifElement(PaImageExf.BitsPerSample)) },
		
		{getGuiStrs("exifTableImageWidthRow"),getImageWidthOrLength(loader.getExifElement(PaImageExf.Width)) },
		
		{getGuiStrs("exifTableImageLengthRow"),getImageWidthOrLength(loader.getExifElement(PaImageExf.Length)) },
		
		{getGuiStrs("exifTableXResolutionRow"),getLongString(loader.getExifElement(PaImageExf.ResX),-1,"XResolution") },
		
		{getGuiStrs("exifTableYResolutionRow"),getLongString(loader.getExifElement(PaImageExf.ResY),-1,"YResolution") },
		
		{getGuiStrs("exifTableRefBlackWhiteRow"),getReferenceBlackWhite(loader.getExifElement(PaImageExf.ReferenceBlackWhite)) },
		
		{getGuiStrs("exifTableFlashPixVerRow"),getString(loader.getExifElement(PaExtendedExf.FlashPixVersion),"FlashPix") },
		
		{getGuiStrs("exifTableExposureTimeRow"),getDoubleString(loader.getExifElement(PaExtendedExf.ExposureTime),5,"ExposureTime") },
		
		{getGuiStrs("exifTableExposureProgramRow"),getExposureProgram(loader.getExifElement(PaExtendedExf.ExposureProgram)) },
		
		{getGuiStrs("exifTableExpModeRow"),getExposureMode(loader.getExifElement(PaExtendedExf.ExposureMode)) },
		
		{getGuiStrs("exifTableLightSourceRow"),getLightSource(loader.getExifElement(PaExtendedExf.LightSource)) },
		
		{getGuiStrs("exifTableFocalLenRow"),getStringUnit(loader.getExifElement(PaExtendedExf.FocalLength),"FocalLength ",getGuiStrs("UnitsMM")) },
		
		{getGuiStrs("exifTableScreenCapTypeRow"),getSceneCaptureType(loader.getExifElement(PaExtendedExf.SceneCaptureType)) },
		
		{getGuiStrs("exifTableDigZoomRatioRow"),getDigitalZoomRation(loader.getExifElement(PaExtendedExf.DigitalZoomRatio)) },
		
		{getGuiStrs("exifTablePhInterpretationRow"),getPhotometricInter(loader.getExifElement(PaImageExf.PhotometricInterpretation)) },
		
		{getGuiStrs("exifTableSamplesPerPixelRow"),getString(loader.getExifElement(PaImageExf.SamplesPerPixel),"SamplesPerPixel") },
		
		{getGuiStrs("exifTablePlanarRow"),getPlanar(loader.getExifElement(PaImageExf.Planar)) },
		
		{getGuiStrs("exifTableCopyrightRow"),getString(loader.getExifElement(PaImageExf.Copyright),"Copyright") },
		
		{getGuiStrs("exifTableYCbCrSubsamplingRow"),getYCbCrSubsampling(loader.getExifElement(PaImageExf.YCbCrSubSampling)) },
		
		{getGuiStrs("exifTableDateTimeOriginalRow"),getString(loader.getExifElement(PaExtendedExf.DateTimeOriginal),"DateTimeOriginal") },
		
		{getGuiStrs("exifTableColorSpaceRow"),getColorSpace(loader.getExifElement(PaExtendedExf.ColorSpace)) },
		
		{getGuiStrs("exifTableSensingMethodRow"),getSensingMethod(loader.getExifElement(PaExtendedExf.SensingMethod)) },
		
		{getGuiStrs("exifTableWhiteBalanceRow"),getWhiteBalance(loader.getExifElement(PaExtendedExf.WhiteBalance)) },
		
		{getGuiStrs("exifTableSubjectAreaRow"),getSubjectArea(loader.getExifElement(PaExtendedExf.SubjectArea)) },
		
		{getGuiStrs("exifTableFlashEnergyRow"),getString(loader.getExifElement(PaExtendedExf.FlashEnergy),"FlashEnergy") },
		
		{getGuiStrs("exifTableFocalPlaneXResRow"),getLongString(loader.getExifElement(PaExtendedExf.FocalPlaneXResolution),-1,"FocalPlaneXResolution") },
		
		{getGuiStrs("exifTableFocalPlaneYResRow"),getLongString(loader.getExifElement(PaExtendedExf.FocalPlaneYResolution),-1,"FocalPlaneYResolution") },
		
		{getGuiStrs("exifTableSubDisRangeRow"),getSubDistanceRange(loader.getExifElement(PaExtendedExf.SubjectDistanceRange)) },
		
		{getGuiStrs("exifTableImageUniqueIdRow"),getString(loader.getExifElement(PaExtendedExf.ImageUniqueId ),"ImageUniqueId") },
	
		{getGuiStrs("exifTableSharpnessRow"),getSharpness(loader.getExifElement(PaExtendedExf.Sharpness)) },
		
		{getGuiStrs("exifTableSaturationRow"),getSaturation(loader.getExifElement(PaExtendedExf.Saturation)) },
		
		{getGuiStrs("exifTableContrastRow"),getContrast(loader.getExifElement(PaExtendedExf.Contrast)) },
		
		{getGuiStrs("exifTableMeteringModeRow"),getMeteringMode(loader.getExifElement(PaExtendedExf.MeteringMode)) },
		
		{getGuiStrs("exifTableImageDescRow"),getString(loader.getExifElement(PaImageExf.ImageDesc),"ImageDesc") }
		};
		
		String no_data = getGuiStrs("noDataString");
		
		int counter = 0;
		
		for(int i = 0;  i < data.length; ++i){
			
			if(!data[i][1].equals(no_data)){
				
				++counter;
			}
		}
		Object[][] data1 = new Object[counter][2];
		
		counter = 0;
		
		for(int i = 0;  i < data.length; ++i){
			
			if(!data[i][1].equals(no_data)){
				
				data1[counter][0] = data[i][0];
				
				data1[counter][1] = data[i][1];
				
				++counter;
			}
		}
		return data1;
	} 
	
}

