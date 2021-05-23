package apdc.events.utils.moreAttributes;

import javax.ws.rs.core.Response.Status;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Transaction;

import apdc.tpc.utils.AdditionalAttributes;
import apdc.tpc.utils.StorageMethods;
import apdc.utils.conts.Constants;

public class AdditionalAttributesOperations {
	public static final String ADITIONALS = "ADDITIONALS_INFO";
	private static final String PERFIL = "PERFIL";
	private static final String LOCALIDADE = "LOCALIDADE";
	private static final String MORADA_COMPLEMENTAR = "MORADA_COMPLEMENTAR";
	private static final String MORADA = "MORADA";
	private static final String TELEFONE = "TELEFONE";
	private static final String TELEMOVEL = "TELEMOVEL";
	public AdditionalAttributesOperations() {
		// TODO Auto-generated constructor stub
	}

	public static boolean removeAdditionalAttributes(Datastore datastore, long userid) {
		boolean result=false;
		com.google.cloud.datastore.Key ctrsKey=datastore.newKeyFactory().setKind(ADITIONALS).newKey(userid);
		
		Transaction txn = datastore.newTransaction();
		  try {
			txn.delete(ctrsKey);
		    txn.commit();
		    result=true;
		  }catch(Exception e) {
			Constants.LOG.severe(e.getLocalizedMessage());
		  } finally {
			  StorageMethods.rollBack(txn);
		  }
		  return result;
	}
	/**
	 * gets the additional information of the user passed in the argument
	 * @param datastore - datastore instance
	 * @param userid - userid
	 * @return user additional information if success else null
	 */
	public static AdditionalAttributes getAdditionalInfos(Datastore datastore, long userid) {
		com.google.cloud.datastore.Key ctrsKey=datastore.newKeyFactory().setKind(ADITIONALS).newKey(userid);
		 AdditionalAttributes ad=null;
		
		Transaction txn = datastore.newTransaction();
		  try {
			Entity stats=datastore.get(ctrsKey);
			ad = new AdditionalAttributes();
			ad.setPerfil(stats.getString(PERFIL));
			ad.setTelephone(stats.getString(TELEFONE));
			ad.setCellphone(stats.getString(TELEMOVEL));
			ad.setAddress(stats.getString(MORADA));
			ad.setMore_address(stats.getString(MORADA_COMPLEMENTAR));
			ad.setLocality(stats.getString(LOCALIDADE));
		  }catch(Exception e) {
			  print(e.getLocalizedMessage());
		  } finally {
			  StorageMethods.rollBack(txn);
		  }
		  return ad;
	}
	public static Status addUserAdditionalInformation(Datastore datastore, AdditionalAttributes ad, long userid) {
		Status result;
		com.google.cloud.datastore.Key ctrsKey=datastore.newKeyFactory().setKind(ADITIONALS).newKey(userid);
		
		Transaction txn = datastore.newTransaction();
		  try {
			Entity stats;
			stats=Entity.newBuilder(ctrsKey)
					.set(PERFIL,ad.getPerfil())
					.set(TELEFONE,ad.getTelephone())
					.set(TELEMOVEL,ad.getCellphone())
					.set(MORADA,ad.getAddress())
					.set(MORADA_COMPLEMENTAR,ad.getMore_address())
					.set(LOCALIDADE,ad.getLocality())
					.build();
			txn.put(stats);
		    txn.commit();
		    result=Status.OK;
		  }catch(Exception e) {
			result=Status.BAD_REQUEST;
			print(e.getLocalizedMessage());
		  } finally {
			  StorageMethods.rollBack(txn);
		  }
		  return result;
	}
	public static AdditionalAttributes getAdditionalAttributes(Datastore datastore, long userid) {
		com.google.cloud.datastore.Key ctrsKey=datastore.newKeyFactory().setKind(ADITIONALS).newKey(userid);
		  Entity stats=datastore.get(ctrsKey);
		  AdditionalAttributes ad=null;
		  if(stats!=null) {
			  ad = new AdditionalAttributes();
			  //,stats.getString(TELEFONE),stats.getString(TELEMOVEL),stats.getString(MORADA),stats.getString(MORADA_COMPLEMENTAR),stats.getString(LOCALIDADE)
			  ad.setPerfil(stats.getString(PERFIL));
			  ad.setCellphone(stats.getString(TELEMOVEL));
			  ad.setTelephone(stats.getString(TELEFONE));
			  ad.setAddress(stats.getString(MORADA));
			  ad.setMore_address(stats.getString(MORADA_COMPLEMENTAR));
			  ad.setLocality(stats.getString(LOCALIDADE));
		  }
		return ad;
	}
	private static void print(String msg) {
		Constants.LOG.severe(msg);
	}
}
