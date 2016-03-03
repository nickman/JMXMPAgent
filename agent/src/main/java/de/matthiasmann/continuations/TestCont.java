package de.matthiasmann.continuations;

import java.io.File;
import java.io.Serializable;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.Iterator;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.util.CheckClassAdapter;

import com.heliosapm.shorthand.attach.vm.agent.LocalAgentInstaller;
import com.heliosapm.utils.time.SystemClock;
import com.heliosapm.utils.url.URLHelper;

import de.matthiasmann.continuations.instrument.DBClassWriter;
import de.matthiasmann.continuations.instrument.InstrumentClass;
import de.matthiasmann.continuations.instrument.LogLevel;
import de.matthiasmann.continuations.instrument.MethodDatabase;

public class TestCont {

	public TestCont() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		log("TestCont");
		final Instrumentation instr = LocalAgentInstaller.getInstrumentation();
		final Transformer tran = new Transformer(new MethodDatabase(TestCont.class.getClassLoader()), false);
		instr.addTransformer(tran, true);
		try {
			instr.retransformClasses(TestIterator.class);
			instr.retransformClasses(TestCont.class);
			log("Class transformed");
		} catch (Exception ex) {
			ex.printStackTrace(System.err);
			throw new RuntimeException(ex);
		} finally {
			instr.removeTransformer(tran);
		}
		Iterator<String> iter1 = new TestIterator();
		while(iter1.hasNext()) {
		    System.out.println(iter1.next());
		}
		final SimpleObjectIter iter = new SimpleObjectIter();
		final Thread t = new Thread() {
			public void run() {
				SystemClock.sleep(2000);
				log("Setting...");
				iter.value = "Hello World";
				log("Set");
			}
		};
		t.start();
		try {
			t.join();
			log("NEXT:" + iter.next());
		} catch (Exception ex) {
			ex.printStackTrace(System.err);
		}

	}
	
	public static void log(Object msg) {
		System.out.println(msg);
	}
	
	public static class TestIterator extends CoIterator<String> implements Serializable {
    @Override
    protected void run() throws SuspendExecution {
        produce("A");
        produce("B");
        for(int i = 0; i < 4; i++) {
            produce("C" + i);
        }
        produce("D");
        produce("E");
    }
}	
	
	public static class SimpleObjectIter extends CoIterator<Object> {
		protected Object value = null;
		@Override
		protected void run() throws SuspendExecution {
			produce(value);			
		}	
		protected void setValue(final Object v) {
			value = v;
			
		}
	}
	
  private static class Transformer implements ClassFileTransformer {
    private final MethodDatabase db;
    private final boolean check;

    public Transformer(MethodDatabase db, boolean check) {
        this.db = db;
        this.check = check;
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
            ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        if(MethodDatabase.isJavaCore(className)) {
            return null;
        }
        if(className.startsWith("org/objectweb/asm/")) {
            return null;
        }

        db.log(LogLevel.INFO, "TRANSFORM: %s", className);

        try {
            byte[] bytecode = instrumentClass(db, classfileBuffer, check);
            File file = new File("/tmp/" + className + ".class");
            file.getParentFile().mkdirs();
            if(!file.exists()) {
            	file.createNewFile();
            } else {
            	
            }
            
            URLHelper.writeToURL(URLHelper.toURL(file), bytecode, false);
            log("Wrote class file [" + file + "]");
            
            return bytecode;
        } catch(Exception ex) {
            db.error("Unable to instrument", ex);
            return null;
        }
    }
    
    static byte[] instrumentClass(MethodDatabase db, byte[] data, boolean check) {
      ClassReader r = new ClassReader(data);
      ClassWriter cw = new DBClassWriter(db, r);
      ClassVisitor cv = check ? new CheckClassAdapter(cw) : cw;
      InstrumentClass ic = new InstrumentClass(cv, db, false);
      r.accept(ic, ClassReader.SKIP_FRAMES);
      return cw.toByteArray();
  }
    
}
	

}
