package be.ac.ua.ansymo.cheopsj.changerecorders.mock;

import org.evolizer.changedistiller.model.classifiers.EntityType;
import org.evolizer.changedistiller.model.entities.SourceCodeEntity;
import static org.mockito.Mockito.*;

public class MockSourceCodeEntity {

	public static SourceCodeEntity getMockFieldEntity(String name){
		SourceCodeEntity e = mock(SourceCodeEntity.class);
		when(e.getUniqueName()).thenReturn(name);
		when(e.getModifiers()).thenReturn(0);
		
		EntityType type = mock(EntityType.class);
		when(type.isField()).thenReturn(true);
		
		when(e.getType()).thenReturn(type);
		
		return e;
	}
	
	
	public static SourceCodeEntity getMockTypeEntity(String name){
		SourceCodeEntity e = mock(SourceCodeEntity.class);
		when(e.getUniqueName()).thenReturn(name);
		when(e.getModifiers()).thenReturn(0);
		
		EntityType type = mock(EntityType.class);
		when(type.isType()).thenReturn(true);
		
		when(e.getType()).thenReturn(type);
		
		return e;
	}
	
}
