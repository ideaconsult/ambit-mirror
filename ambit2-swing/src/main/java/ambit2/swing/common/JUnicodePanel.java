/*
Copyright (C) 2005-2006  

Contact: nina@acad.bg

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public License
as published by the Free Software Foundation; either version 2.1
of the License, or (at your option) any later version.
All we ask is that proper credit is given for our work, which includes
- but is not limited to - adding the above copyright notice to the beginning
of your source code files, and to any copyright notice that you may distribute
with programs based on this work.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA
*/

package ambit2.swing.common;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;

/*
 * Free character map utility
 * http://tlt.its.psu.edu/suggestions/international/accents/charmap.html
 */
public class JUnicodePanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2447213976956166867L;
	protected UnicodeTable model;
	protected JTable table; 
	protected Font font = new Font("serif", Font.BOLD, 18); 
	protected int codepoint = 32;
	
	public JUnicodePanel() {
		super(new BorderLayout());
		addWidgets();
	}
	protected void addWidgets() {
		final String[] subsets = new String[] {
				"Basic Latin",
				"Basic Greek",
				"Greek Extended",
				"Symbols and Punctuation",
				"Superscripts and Subscripts ",
				"Currency Symbols",
				"Arrows",
				"Mathematical Operators",
				"Miscellaneous Technical"
				};

		final int[] offsets = new int[] 
			{	0x0020,
				0x0370,
				0x1F00,
				0x200,
				0x2070,
				0x20A0,
				0x2190,
				0x2200,
				0x2300};
		

		final JComboBox box = new JComboBox(subsets);
		
		box.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JComboBox cb = (JComboBox)e.getSource();
				//int startgreek = offsets[cb.getSelectedIndex()] / 16;
				//table.getSelectionModel().setSelectionInterval(startgreek,startgreek);
			    model.setBase((char)offsets[cb.getSelectedIndex()]);
			    table.setRowSelectionInterval(0,0);
				table.scrollRectToVisible(table.getCellRect(0, 0, true));
				
			}
		});
		add(box,BorderLayout.NORTH);
		JToolBar bar = new JToolBar();
		bar.setFloatable(false);
		bar.add(new JLabel("Character code: "));
		final JFormattedTextField f = new JFormattedTextField("");
		f.setEnabled(false);
		bar.add(f);
		
		final JFormattedTextField ft = new JFormattedTextField("");
		ft.setEnabled(true);
		bar.add(new JLabel("Symbol :"));
		bar.add(ft);
		
		add(bar,BorderLayout.SOUTH);
		//model = new UnicodeTable((char) (0x20)); //(char)(0 * 0x100));
		model = new UnicodeTable('\u0000'); //(char)(0 * 0x100));
		table = new JTable(model);
		table.setTableHeader(null);
		table.setFont(font);

		
		//table.setFont(new Font(font.getName(),Font.BOLD,font.getSize()));
		table.setRowHeight(32);
		table.setCellSelectionEnabled(true);
		table.setRowSelectionAllowed(true);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				int row = table.rowAtPoint(e.getPoint());
				int col = table.columnAtPoint(e.getPoint());
				codepoint = model.getCodePoint(row, col);
				f.setText(Integer.toString(codepoint,16));
				ft.setText(table.getValueAt(row, col).toString());
				ft.selectAll();
				ft.copy();
						//table.getValueAt(row, col).toString());
			}
		});
		
		add(new JScrollPane(table),BorderLayout.CENTER);
		box.setSelectedIndex(1);
		/*
	    model.setBase((char)offsets[0]);
	    table.setRowSelectionInterval(0,0);
		table.scrollRectToVisible(table.getCellRect(0, 0, true));
		*/		
		setPreferredSize(new Dimension(16*32, 6*32));
	}
	public int getCodepoint() {
		return codepoint;
	}
	public static Object getChar(int codePoint) {
		if (!Character.isDefined(codePoint)) return "";
		if (Character.charCount(codePoint) == 1) {
	        return (char) codePoint;
	    } else {
	        return Character.toChars(codePoint);
	    }


//		return (char) codePoint;
		/*
		StringBuilder b = new StringBuilder();
		b.appendCodePoint(codePoint); 		
		return b.toString();
		*/
	}	
}
class UnicodeTable extends AbstractTableModel {
	//protected int page;
	protected char base; 
	//protected int start = (int)base & 0xFFF0;
	protected char start = base; //(char)base & 0x0000;
	public UnicodeTable(char base) {
		setBase(base);
	}
	public int getColumnCount() {
		return 16;
	}

	public int getRowCount() {
		return 4096;
	}
	public int getCodePoint(int rowIndex, int columnIndex) {
		return start + rowIndex*16 + columnIndex;
	}
	

	public Object getValueAt(int rowIndex, int columnIndex) {
		return JUnicodePanel.getChar(getCodePoint(rowIndex, columnIndex));
		//return JUnicodePanel.getChar(getCodePoint(rowIndex, columnIndex)).toString();
		/*
		//if (Character.charCount(codepoint) == 1) 
		char[] chars = Character.toChars(codePoint);
		if (chars.length == 1)
			return chars[0];
		else
			return chars[0]+chars[1];
			*/
	}
	/*
	public void setPage(int page) {
		System.out.println(page);
		this.page = page;
		setBase((char)(page * 0x100));
	}
	*/
	public void setBase(char base) { 
		this.base = base;
		start = base;// & 0xFFF0; 		
		fireTableDataChanged(); 
	}
	/*
	 * Start End Description 
0000 1FFF Alphabets 
0000 007F Basic Latin 
0080 00FF Latin-1 Supplement 
0100 017F Latin Extended-A 
0180 024F Latin Extended-B 
0250 02AF IPA Extensions 
02B0 02FF Spacing Modifier Letters 
0300 036F Combining Diacritical Marks 
0370 03FF Greek 
0400 04FF Cyrillic 
0530 058F Armenian 
0590 05FF Hebrew 
0600 06FF Arabic 
0900 097F Devanagari 
0980 09FF Bengali 
0A00 0A7F Gurmukhi 
0A80 0AFF Gujarati 
0B00 0B7F Oriya 
0B80 0BFF Tamil 
0C00 0C7F Telugu 
0C80 0CFF Kannada 
0D00 0D7F Malayalam 
0E00 0E7F Thai 
0E80 0EFF Lao 
0F00 0FBF Tibetan 
10A0 10FF Georgian 
1100 11FF Hangul Jamo 
1E00 1EFF Latin Extended Additional 
1F00 1FFF Greek Extended 
2000 2FFF Symbols and Punctuation 
2000 206F General Punctuation 
2070 209F Superscripts and Subscripts 
20A0 20CF Currency Symbols 
20D0 20FF Combining Marks for Symbols 
2100 214F Letterlike Symbols 
2150 218F Number Forms 
2190 21FF Arrows 
2200 22FF Mathematical Operators 
2300 23FF Miscellaneous Technical 
2400 243F Control Pictures 
2440 245F Optical Character Recognition 
2460 24FF Enclosed Alphanumerics 
2500 257F Box Drawing 
2580 259F Block Elements 
25A0 25FF Geometric Shapes 
2600 26FF Miscellaneous Symbols 
2700 27BF Dingbats 
3000 33FF CJK Auxiliary 
3000 303F CJK Symbols and Punctuation 
3040 309F Hiragana 
30A0 30FF Katakana 
3100 312F Bopomofo 
3130 318F Hangul Compatibility Jamo 
3190 319F Kanbun 
3200 32FF Enclosed CJK Letters and Months 
3300 33FF CJK Compatibility 
4E00 9FFF CJK Unified Ideographs Han characters used in China, Japan, Korea, Taiwan, and Vietnam
 
AC00 D7A3 Hangul Syllables 
D800 DFFF Surrogates 
D800 DB7F High Surrogates 
DB80 DBFF High Private Use Surrogates 
DC00 DFFF Low Surrogates 
E000 F8FF Private Use 
F900 FFFF Miscellaneous 
F900 FAFF CJK Compatibility Ideographs 
FB00 FB4F Alphabetic Presentation Forms 
FB50 FDFF Arabic Presentation Forms-A 
FE20 FE2F Combining Half Marks 
FE30 FE4F CJK Compatibility Forms 
FE50 FE6F Small Form Variants 
FE70 FEFE Arabic Presentation Forms-B 
FEFF FEFF Specials 
FF00 FFEF Halfwidth and Fullwidth Forms 
FFF0 FFFF Specials 

	 */
	
}


