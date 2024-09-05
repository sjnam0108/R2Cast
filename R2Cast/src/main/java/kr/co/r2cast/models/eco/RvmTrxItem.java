package kr.co.r2cast.models.eco;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import kr.co.r2cast.models.fnd.Site;

@Entity
@Table(name="ECO_RVM_TRX_ITEMS", uniqueConstraints = {
        @javax.persistence.UniqueConstraint(columnNames = {"SITE_ID", "RVM_ID", "RVM_TRX_ID", "TIME"})
})
public class RvmTrxItem {
	
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "rvm_trx_item_seq_gen")
    @SequenceGenerator(name = "rvm_trx_item_seq_gen", sequenceName = "ECO_RVM_TRX_ITEMS_SEQ")
    @Column(name = "RVM_TRX_ITEM_ID")
    private int id;
    
    
    /*
    <Item>	
    	<GroupId>5</GroupId>	
    	<Count>1</Count>	
    	<Amount>130</Amount>	
    	<Vat>0</Vat>	
    	<Type>0</Type>	
    	<Barcode>8801858044701</Barcode>	
    	<Time>20221019173205148</Time>	
    	<EmptiesType>TYPE-REFILLABLE</EmptiesType>	
  	</Item>	
    */
    
    // 수거일(시간 미포함): 특정일 기준 자료 query 목적
    @Column(name = "OP_DATE", nullable = false)
    private Date opDate;
	
    // 유형번호 - XML / GroupId 저장
    @Column(name = "GROUP_ID", nullable = false)
    private int groupId;
	
    // 갯수 - XML / Count 저장
    @Column(name = "COUNT", nullable = false)
    private int count;
	
    // 단가 - XML / Amount 저장
    @Column(name = "AMOUNT", nullable = false)
    private int amount;
	
    // 부가세 - XML / Vat 저장
    @Column(name = "VAT", nullable = false)
    private int vat;
	
    // 리필유형 - XML / Type 저장
    // 0: Refillable
    // 1: Non-refillable. Within a clearing system, containers that belong to this clearing system
    // >1: Within a clearing system, non-refillable containers that do not belong to this clearing system.
    @Column(name = "TYPE", nullable = false, length = 1)
    private String type = "0";
	
    // 바코드 - XML / Barcode 저장
    @Column(name = "BARCODE", length = 50)
    private String barcode;
	
    // 시간 - XML / Time 저장
    @Column(name = "TIME", length = 20)
    private String time;
	
    // 리필유형 설명 - XML / EmptiesType 저장
    @Column(name = "EMPTIES_TYPE", length = 50)
    private String emptiesType;


    @Column(name = "CREATION_DATE", nullable = false)
    private Date whoCreationDate;
	
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "SITE_ID", nullable = false)
    private Site site;
    
    @ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "RVM_ID", nullable = false)
	private Rvm rvm;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "RVM_TRX_ID", nullable = false)
    private RvmTrx rvmTrx;

    
    
    public RvmTrxItem() {}
    
    public RvmTrxItem(RvmTrx rvmTrx, int groupId, int count, int amount, int vat, String type, String barcode, String time, String emptiesType) {
    	
    	this.site = rvmTrx.getSite();
    	this.rvm = rvmTrx.getRvm();
    	this.rvmTrx = rvmTrx;
		this.groupId = groupId;
		this.count = count;
		this.amount = amount;
		this.vat = vat;
		this.type = type;
		this.barcode = barcode;
		this.time = time;
		this.emptiesType = emptiesType;
		
		this.opDate = rvmTrx.getOpDate();
    	
		this.whoCreationDate = new Date();
    }

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getOpDate() {
		return opDate;
	}

	public void setOpDate(Date opDate) {
		this.opDate = opDate;
	}

	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public int getVat() {
		return vat;
	}

	public void setVat(int vat) {
		this.vat = vat;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getEmptiesType() {
		return emptiesType;
	}

	public void setEmptiesType(String emptiesType) {
		this.emptiesType = emptiesType;
	}

	public Date getWhoCreationDate() {
		return whoCreationDate;
	}

	public void setWhoCreationDate(Date whoCreationDate) {
		this.whoCreationDate = whoCreationDate;
	}

	public Site getSite() {
		return site;
	}

	public void setSite(Site site) {
		this.site = site;
	}

	public RvmTrx getRvmTrx() {
		return rvmTrx;
	}

	public void setRvmTrx(RvmTrx rvmTrx) {
		this.rvmTrx = rvmTrx;
	}

	public Rvm getRvm() {
		return rvm;
	}

	public void setRvm(Rvm rvm) {
		this.rvm = rvm;
	}

}
