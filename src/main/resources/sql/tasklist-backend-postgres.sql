PGDMP       6            
    |            aufgabe    13.16    16.4 ?               0    0    ENCODING    ENCODING        SET client_encoding = 'UTF8';
                      false                       0    0 
   STDSTRINGS 
   STDSTRINGS     (   SET standard_conforming_strings = 'on';
                      false                       0    0 
   SEARCHPATH 
   SEARCHPATH     8   SELECT pg_catalog.set_config('search_path', '', false);
                      false                       1262    16660    aufgabe    DATABASE     {   CREATE DATABASE aufgabe WITH TEMPLATE = template0 ENCODING = 'UTF8' LOCALE_PROVIDER = libc LOCALE = 'Russian_Russia.1252';
    DROP DATABASE aufgabe;
                postgres    false                        2615    16661    tasklist    SCHEMA        CREATE SCHEMA tasklist;
    DROP SCHEMA tasklist;
                postgres    false            �            1255    16662    add_user_stat_test_data()    FUNCTION     �  CREATE FUNCTION tasklist.add_user_stat_test_data() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
DECLARE
	
	/* для хранения id вставленных тестовых данных - чтобы их можно было использовать при создании тестовых задач*/
	priorId1 INTEGER; 
	priorId2 INTEGER;
	priorId3 INTEGER;
	
	catId1 INTEGER;
	catId2 INTEGER;
	catId3 INTEGER;
	
	/* тестовые даты */
	date1 Date = NOW() + INTERVAL '1 day';
	date2 Date = NOW();
	date3 Date = NOW() + INTERVAL '6 day';


BEGIN

		 /* при вставке нового пользователя - создаем строку для хранения статистики */
    insert into tasklist.stat (completed_total, uncompleted_total, user_id) values (0,0, new.id);
    
	/* добавляем начальные тестовые категории для созданного пользователя */
    insert into tasklist.category (title, completed_count, uncompleted_count, user_id) values ('Family',0 ,0 ,new.id) RETURNING id into catId1; /* сохранить id вставленной записи в переменную */
    insert into tasklist.category (title, completed_count, uncompleted_count, user_id) values ('Work',0 ,0 ,new.id) RETURNING id into catId2;
	insert into tasklist.category (title, completed_count, uncompleted_count, user_id) values ('Rest',0 ,0 ,new.id) RETURNING id into catId3;
	insert into tasklist.category (title, completed_count, uncompleted_count, user_id) values ('Trips',0 ,0 ,new.id);
    insert into tasklist.category (title, completed_count, uncompleted_count, user_id) values ('Sport',0 ,0 ,new.id);
    insert into tasklist.category (title, completed_count, uncompleted_count, user_id) values ('Health',0 ,0 ,new.id);



	/* добавляем начальные тестовые приоритеты для созданного пользователя */
    insert into tasklist.priority (title, color, user_id) values ('Short', '#caffdd', new.id) RETURNING id into priorId1;
    insert into tasklist.priority (title, color, user_id) values ('Average', '#883bdc', new.id) RETURNING id into priorId2;
    insert into tasklist.priority (title, color, user_id) values ('High', '#f05f5f', new.id) RETURNING id into priorId3;



    	
	/* добавляем начальные тестовые задачи для созданного пользователя */
    insert into tasklist.task (title, completed, user_id, priority_id, category_id, task_date) values ('Call your parents', 0, new.id, priorId1, catId1, date1);
    insert into tasklist.task (title, completed, user_id, priority_id, category_id, task_date) values ('Watch cartoons', 1,  new.id, priorId1, catId1, date2);
    insert into tasklist.task (title, completed, user_id, priority_id, category_id) values ('Take Java courses', 0, new.id, priorId3, catId2);
    insert into tasklist.task (title, completed, user_id, priority_id) values ('Make a green smoothie', 1, new.id, priorId3);
    insert into tasklist.task (title, completed, user_id, priority_id, task_date) values ('Buy a loaf of bread', 0, new.id, priorId2, date3);


	
	RETURN NEW;
END;
$$;
 2   DROP FUNCTION tasklist.add_user_stat_test_data();
       tasklist          postgres    false    6            �            1255    16663    delete_task_stat()    FUNCTION     5  CREATE FUNCTION tasklist.delete_task_stat() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
	/* можно было упаковать все условия в один if-else, но тогда он становится не очень читабельным */

    /*  категория НЕПУСТАЯ                 и        статус задачи ЗАВЕРШЕН */
    if (coalesce(old.category_id, 0)>0       and       coalesce(old.completed, 0)=1) then
		update tasklist.category set completed_count = (coalesce(completed_count, 0)-1) where id = old.category_id and user_id=old.user_id;
	end if;
    
	/*  категория НЕПУСТАЯ                и         статус задачи НЕЗАВЕРШЕН */
    if (coalesce(old.category_id, 0)>0      and        coalesce(old.completed, 0)=0) then
		update tasklist.category set uncompleted_count = (coalesce(uncompleted_count, 0)-1) where id = old.category_id and user_id=old.user_id;
	end if;
    
    
    
    /* общая статистика */
	if coalesce(old.completed, 0)=1 then
		update tasklist.stat set completed_total = (coalesce(completed_total, 0)-1)  where user_id=old.user_id;
	else
		update tasklist.stat set uncompleted_total = (coalesce(uncompleted_total, 0)-1)  where user_id=old.user_id;
    end if;
	
	RETURN OLD;
    
END
$$;
 +   DROP FUNCTION tasklist.delete_task_stat();
       tasklist          postgres    false    6            �            1255    16664    insert_task_stat()    FUNCTION       CREATE FUNCTION tasklist.insert_task_stat() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN

	/* можно было упаковать все условия в один if-else, но тогда он становится не очень читабельным */
    
    /*  категория НЕПУСТАЯ                и       статус задачи ЗАВЕРШЕН */
    if (coalesce(NEW.category_id, 0)>0 and NEW.completed=1     ) then
		update tasklist.category set completed_count = (coalesce(completed_count, 0)+1) where id = NEW.category_id and user_id=new.user_id;
	end if;
	
	
	/*  категория НЕПУСТАЯ                 и       статус задачи НЕЗАВЕРШЕН */
    if (coalesce(NEW.category_id, 0)>0      and      coalesce(NEW.completed, 0) = 0) then
		update tasklist.category set uncompleted_count = (coalesce(uncompleted_count, 0)+1) where id = NEW.category_id and user_id=new.user_id;
	end if;
    

    /* общая статистика */
	if coalesce(NEW.completed, 0) = 1 then
		update tasklist.stat set completed_total = (coalesce(completed_total, 0)+1)  where user_id=new.user_id;
	else
		update tasklist.stat set uncompleted_total = (coalesce(uncompleted_total, 0)+1)  where user_id=new.user_id;
    end if;

    

	RETURN NEW;

END
$$;
 +   DROP FUNCTION tasklist.insert_task_stat();
       tasklist          postgres    false    6            �            1255    16665    update_task_stat()    FUNCTION     V  CREATE FUNCTION tasklist.update_task_stat() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN

  /* изменили completed на 1, НЕ изменили категорию */
    IF (   coalesce(old.completed,0)=0  and   new.completed=1      and   coalesce(old.category_id,0) =coalesce(new.category_id,0)     ) THEN    
    
		/* у категории кол-во незавершенных уменьшится на 1,  кол-во завершенных увеличится на 1 */
		update tasklist.category set uncompleted_count = (coalesce(uncompleted_count, 0)-1), completed_count = (coalesce(completed_count,0)+1) where id = old.category_id and user_id=old.user_id; 
        
        /* общая статистика */
		update tasklist.stat set uncompleted_total = (coalesce(uncompleted_total,0)-1), completed_total = (coalesce(completed_total,0)+1)  where user_id=old.user_id;

        
	END IF;
    
    
    /* изменили completed на 0, НЕ изменили категорию */
    IF (   coalesce(old.completed,1) =1   and   new.completed=0       and   coalesce(old.category_id,0) = coalesce(new.category_id,0)   ) THEN    
    
		/* у категории кол-во завершенных уменьшится на 1, кол-во незавершенных увеличится на 1 */
		update tasklist.category set completed_count = (coalesce(completed_count,0)-1), uncompleted_count = (coalesce(uncompleted_count,0)+1) where id = old.category_id and user_id=old.user_id; 
       
       /* общая статистика */
		update tasklist.stat set completed_total = (coalesce(completed_total,0)-1), uncompleted_total = (coalesce(uncompleted_total,0)+1)  where user_id=old.user_id;

       
	END IF;
    
    
    
	/* изменили категорию для неизмененного completed=1 */
    IF (   coalesce(old.completed,1) = 1    and   new.completed=1       and   coalesce(old.category_id,0) <> coalesce(new.category_id,0)    ) THEN    
    
		/* у старой категории кол-во завершенных уменьшится на 1 */
		update tasklist.category set completed_count = (coalesce(completed_count,0)-1) where id = old.category_id and user_id=old.user_id; 

        
		/* у новой категории кол-во завершенных увеличится на 1 */
		update tasklist.category set completed_count = (coalesce(completed_count,0)+1) where id = new.category_id and user_id=old.user_id; 
	
    
		/* общая статистика не изменяется */
       
	END IF;
    
    
    
    
        
    /* изменили категорию для неизменнеого completed=0 */
    IF (   coalesce(old.completed,0) = 0     and   new.completed=0      and   coalesce(old.category_id,0) <> coalesce(new.category_id,0)     ) THEN    
    
		/* у старой категории кол-во незавершенных уменьшится на 1 */
		update tasklist.category set uncompleted_count = (coalesce(uncompleted_count,0)-1) where id = old.category_id and user_id=old.user_id; 

		/* у новой категории кол-во незавершенных увеличится на 1 */
		update tasklist.category set uncompleted_count = (coalesce(uncompleted_count,0)+1) where id = new.category_id and user_id=old.user_id; 
       
       /* общая статистика не изменяется */
       
	END IF;
    
    
    
    
    
	
    /* изменили категорию, изменили completed с 1 на 0 */
    IF (   coalesce(old.completed,1) =1     and   new.completed=0      and   coalesce(old.category_id,0) <> coalesce(new.category_id,0)     ) THEN    
    
		/* у старой категории кол-во завершенных уменьшится на 1 */
		update tasklist.category set completed_count = (coalesce(completed_count,0)-1) where id = old.category_id and user_id=old.user_id; 
        
		/* у новой категории кол-во незавершенных увеличится на 1 */
		update tasklist.category set uncompleted_count = (coalesce(uncompleted_count,0)+1) where id = new.category_id and user_id=old.user_id; 


		/* общая статистика */
		update tasklist.stat set uncompleted_total = (coalesce(uncompleted_total,0)+1), completed_total = (coalesce(completed_total,0)-1)  where user_id=old.user_id;

       
	END IF;
    
    
            
    /* изменили категорию, изменили completed с 0 на 1 */
    IF (   coalesce(old.completed,0) =0     and   new.completed=1      and   coalesce(old.category_id,0) <> coalesce(new.category_id,0)     ) THEN    
    
		/* у старой категории кол-во незавершенных уменьшится на 1 */
		update tasklist.category set uncompleted_count = (coalesce(uncompleted_count,0)-1) where id = old.category_id and user_id=old.user_id; 
        
		/* у новой категории кол-во завершенных увеличится на 1 */
		update tasklist.category set completed_count = (coalesce(completed_count,0)+1) where id = new.category_id and user_id=old.user_id; 
        
        /* общая статистика */
		update tasklist.stat set uncompleted_total = (coalesce(uncompleted_total,0)-1), completed_total = (coalesce(completed_total,0)+1)  where user_id=old.user_id;
	
	END IF;
    
    
	

	
	RETURN NEW;
	
	END;
$$;
 +   DROP FUNCTION tasklist.update_task_stat();
       tasklist          postgres    false    6            �            1259    16666    activity    TABLE     �   CREATE TABLE tasklist.activity (
    id bigint NOT NULL,
    activated smallint NOT NULL,
    uuid text NOT NULL,
    user_id bigint NOT NULL
);
    DROP TABLE tasklist.activity;
       tasklist         heap    postgres    false    6            �            1259    16672    activity_id_seq    SEQUENCE     �   ALTER TABLE tasklist.activity ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME tasklist.activity_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            tasklist          postgres    false    201    6            �            1259    16674    category    TABLE     �   CREATE TABLE tasklist.category (
    id bigint NOT NULL,
    title text NOT NULL,
    completed_count bigint,
    uncompleted_count bigint,
    user_id bigint NOT NULL
);
    DROP TABLE tasklist.category;
       tasklist         heap    postgres    false    6            �            1259    16680    category_id_seq    SEQUENCE     �   ALTER TABLE tasklist.category ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME tasklist.category_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            tasklist          postgres    false    6    203            �            1259    16682    priority    TABLE     �   CREATE TABLE tasklist.priority (
    id bigint NOT NULL,
    title text NOT NULL,
    color text NOT NULL,
    user_id bigint NOT NULL
);
    DROP TABLE tasklist.priority;
       tasklist         heap    postgres    false    6            �            1259    16688    priority_id_seq    SEQUENCE     �   ALTER TABLE tasklist.priority ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME tasklist.priority_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            tasklist          postgres    false    6    205            �            1259    16690 	   role_data    TABLE     T   CREATE TABLE tasklist.role_data (
    id bigint NOT NULL,
    name text NOT NULL
);
    DROP TABLE tasklist.role_data;
       tasklist         heap    postgres    false    6            �            1259    16696    role_data_id_seq    SEQUENCE     �   ALTER TABLE tasklist.role_data ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME tasklist.role_data_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            tasklist          postgres    false    6    207            �            1259    16698    stat    TABLE     �   CREATE TABLE tasklist.stat (
    id bigint NOT NULL,
    completed_total bigint NOT NULL,
    uncompleted_total bigint NOT NULL,
    user_id bigint NOT NULL
);
    DROP TABLE tasklist.stat;
       tasklist         heap    postgres    false    6            �            1259    16701    stat_id_seq    SEQUENCE     �   ALTER TABLE tasklist.stat ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME tasklist.stat_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            tasklist          postgres    false    6    209            �            1259    16703    task    TABLE     �   CREATE TABLE tasklist.task (
    id bigint NOT NULL,
    title text NOT NULL,
    completed smallint,
    task_date timestamp without time zone,
    priority_id bigint,
    category_id bigint,
    user_id bigint NOT NULL
);
    DROP TABLE tasklist.task;
       tasklist         heap    postgres    false    6            �            1259    16709    task_id_seq    SEQUENCE     �   ALTER TABLE tasklist.task ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME tasklist.task_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            tasklist          postgres    false    211    6            �            1259    16711 	   user_data    TABLE     �   CREATE TABLE tasklist.user_data (
    id bigint NOT NULL,
    email text NOT NULL,
    password text NOT NULL,
    username text NOT NULL
);
    DROP TABLE tasklist.user_data;
       tasklist         heap    postgres    false    6            �            1259    16717    user_id_seq    SEQUENCE     �   ALTER TABLE tasklist.user_data ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME tasklist.user_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            tasklist          postgres    false    213    6            �            1259    16719 	   user_role    TABLE     ^   CREATE TABLE tasklist.user_role (
    user_id bigint NOT NULL,
    role_id bigint NOT NULL
);
    DROP TABLE tasklist.user_role;
       tasklist         heap    postgres    false    6            �          0    16666    activity 
   TABLE DATA           B   COPY tasklist.activity (id, activated, uuid, user_id) FROM stdin;
    tasklist          postgres    false    201   �t       �          0    16674    category 
   TABLE DATA           \   COPY tasklist.category (id, title, completed_count, uncompleted_count, user_id) FROM stdin;
    tasklist          postgres    false    203   �t       �          0    16682    priority 
   TABLE DATA           ?   COPY tasklist.priority (id, title, color, user_id) FROM stdin;
    tasklist          postgres    false    205   �t                  0    16690 	   role_data 
   TABLE DATA           /   COPY tasklist.role_data (id, name) FROM stdin;
    tasklist          postgres    false    207   �t                 0    16698    stat 
   TABLE DATA           Q   COPY tasklist.stat (id, completed_total, uncompleted_total, user_id) FROM stdin;
    tasklist          postgres    false    209   u                 0    16703    task 
   TABLE DATA           d   COPY tasklist.task (id, title, completed, task_date, priority_id, category_id, user_id) FROM stdin;
    tasklist          postgres    false    211   1u                 0    16711 	   user_data 
   TABLE DATA           D   COPY tasklist.user_data (id, email, password, username) FROM stdin;
    tasklist          postgres    false    213   Nu                 0    16719 	   user_role 
   TABLE DATA           7   COPY tasklist.user_role (user_id, role_id) FROM stdin;
    tasklist          postgres    false    215   ku                  0    0    activity_id_seq    SEQUENCE SET     @   SELECT pg_catalog.setval('tasklist.activity_id_seq', 30, true);
          tasklist          postgres    false    202                       0    0    category_id_seq    SEQUENCE SET     A   SELECT pg_catalog.setval('tasklist.category_id_seq', 265, true);
          tasklist          postgres    false    204                       0    0    priority_id_seq    SEQUENCE SET     A   SELECT pg_catalog.setval('tasklist.priority_id_seq', 127, true);
          tasklist          postgres    false    206                       0    0    role_data_id_seq    SEQUENCE SET     @   SELECT pg_catalog.setval('tasklist.role_data_id_seq', 4, true);
          tasklist          postgres    false    208                       0    0    stat_id_seq    SEQUENCE SET     <   SELECT pg_catalog.setval('tasklist.stat_id_seq', 46, true);
          tasklist          postgres    false    210                       0    0    task_id_seq    SEQUENCE SET     =   SELECT pg_catalog.setval('tasklist.task_id_seq', 198, true);
          tasklist          postgres    false    212                       0    0    user_id_seq    SEQUENCE SET     <   SELECT pg_catalog.setval('tasklist.user_id_seq', 51, true);
          tasklist          postgres    false    214            V           2606    16723    activity activity_pkey 
   CONSTRAINT     V   ALTER TABLE ONLY tasklist.activity
    ADD CONSTRAINT activity_pkey PRIMARY KEY (id);
 B   ALTER TABLE ONLY tasklist.activity DROP CONSTRAINT activity_pkey;
       tasklist            postgres    false    201            Z           2606    16725    category category_pkey 
   CONSTRAINT     V   ALTER TABLE ONLY tasklist.category
    ADD CONSTRAINT category_pkey PRIMARY KEY (id);
 B   ALTER TABLE ONLY tasklist.category DROP CONSTRAINT category_pkey;
       tasklist            postgres    false    203            \           2606    16727    priority priority_pkey 
   CONSTRAINT     V   ALTER TABLE ONLY tasklist.priority
    ADD CONSTRAINT priority_pkey PRIMARY KEY (id);
 B   ALTER TABLE ONLY tasklist.priority DROP CONSTRAINT priority_pkey;
       tasklist            postgres    false    205            ^           2606    16729    role_data role_pkey 
   CONSTRAINT     S   ALTER TABLE ONLY tasklist.role_data
    ADD CONSTRAINT role_pkey PRIMARY KEY (id);
 ?   ALTER TABLE ONLY tasklist.role_data DROP CONSTRAINT role_pkey;
       tasklist            postgres    false    207            `           2606    16731    stat stat_pkey 
   CONSTRAINT     N   ALTER TABLE ONLY tasklist.stat
    ADD CONSTRAINT stat_pkey PRIMARY KEY (id);
 :   ALTER TABLE ONLY tasklist.stat DROP CONSTRAINT stat_pkey;
       tasklist            postgres    false    209            b           2606    16733    task task_pkey 
   CONSTRAINT     N   ALTER TABLE ONLY tasklist.task
    ADD CONSTRAINT task_pkey PRIMARY KEY (id);
 :   ALTER TABLE ONLY tasklist.task DROP CONSTRAINT task_pkey;
       tasklist            postgres    false    211            d           2606    16735    user_data uniq_email 
   CONSTRAINT     R   ALTER TABLE ONLY tasklist.user_data
    ADD CONSTRAINT uniq_email UNIQUE (email);
 @   ALTER TABLE ONLY tasklist.user_data DROP CONSTRAINT uniq_email;
       tasklist            postgres    false    213            f           2606    16737    user_data uniq_username 
   CONSTRAINT     X   ALTER TABLE ONLY tasklist.user_data
    ADD CONSTRAINT uniq_username UNIQUE (username);
 C   ALTER TABLE ONLY tasklist.user_data DROP CONSTRAINT uniq_username;
       tasklist            postgres    false    213            X           2606    16739    activity uniq_uuid 
   CONSTRAINT     O   ALTER TABLE ONLY tasklist.activity
    ADD CONSTRAINT uniq_uuid UNIQUE (uuid);
 >   ALTER TABLE ONLY tasklist.activity DROP CONSTRAINT uniq_uuid;
       tasklist            postgres    false    201            h           2606    16741    user_data user_pkey 
   CONSTRAINT     S   ALTER TABLE ONLY tasklist.user_data
    ADD CONSTRAINT user_pkey PRIMARY KEY (id);
 ?   ALTER TABLE ONLY tasklist.user_data DROP CONSTRAINT user_pkey;
       tasklist            postgres    false    213            j           2606    16743    user_role user_role_pkey 
   CONSTRAINT     ]   ALTER TABLE ONLY tasklist.user_role
    ADD CONSTRAINT user_role_pkey PRIMARY KEY (user_id);
 D   ALTER TABLE ONLY tasklist.user_role DROP CONSTRAINT user_role_pkey;
       tasklist            postgres    false    215            t           2620    16744    task trigger_stat_after_delete    TRIGGER     �   CREATE TRIGGER trigger_stat_after_delete BEFORE DELETE ON tasklist.task FOR EACH ROW EXECUTE FUNCTION tasklist.delete_task_stat();
 9   DROP TRIGGER trigger_stat_after_delete ON tasklist.task;
       tasklist          postgres    false    211    216            u           2620    16745    task trigger_stat_after_insert    TRIGGER     �   CREATE TRIGGER trigger_stat_after_insert BEFORE INSERT ON tasklist.task FOR EACH ROW EXECUTE FUNCTION tasklist.insert_task_stat();
 9   DROP TRIGGER trigger_stat_after_insert ON tasklist.task;
       tasklist          postgres    false    211    229            v           2620    16746    task trigger_stat_after_update    TRIGGER     �   CREATE TRIGGER trigger_stat_after_update BEFORE UPDATE ON tasklist.task FOR EACH ROW EXECUTE FUNCTION tasklist.update_task_stat();
 9   DROP TRIGGER trigger_stat_after_update ON tasklist.task;
       tasklist          postgres    false    230    211            w           2620    16747 (   user_data trigger_user_stat_after_insert    TRIGGER     �   CREATE TRIGGER trigger_user_stat_after_insert AFTER INSERT ON tasklist.user_data FOR EACH ROW EXECUTE FUNCTION tasklist.add_user_stat_test_data();
 C   DROP TRIGGER trigger_user_stat_after_insert ON tasklist.user_data;
       tasklist          postgres    false    217    213            k           2606    16748    activity fk_activity_user    FK CONSTRAINT     �   ALTER TABLE ONLY tasklist.activity
    ADD CONSTRAINT fk_activity_user FOREIGN KEY (user_id) REFERENCES tasklist.user_data(id) ON UPDATE RESTRICT ON DELETE CASCADE NOT VALID;
 E   ALTER TABLE ONLY tasklist.activity DROP CONSTRAINT fk_activity_user;
       tasklist          postgres    false    201    213    2920            l           2606    16753    category fk_category_user    FK CONSTRAINT     �   ALTER TABLE ONLY tasklist.category
    ADD CONSTRAINT fk_category_user FOREIGN KEY (user_id) REFERENCES tasklist.user_data(id) ON UPDATE RESTRICT ON DELETE CASCADE NOT VALID;
 E   ALTER TABLE ONLY tasklist.category DROP CONSTRAINT fk_category_user;
       tasklist          postgres    false    203    213    2920            m           2606    16758    priority fk_priority_user    FK CONSTRAINT     �   ALTER TABLE ONLY tasklist.priority
    ADD CONSTRAINT fk_priority_user FOREIGN KEY (user_id) REFERENCES tasklist.user_data(id) ON UPDATE RESTRICT ON DELETE CASCADE NOT VALID;
 E   ALTER TABLE ONLY tasklist.priority DROP CONSTRAINT fk_priority_user;
       tasklist          postgres    false    205    213    2920            r           2606    16763    user_role fk_role    FK CONSTRAINT     �   ALTER TABLE ONLY tasklist.user_role
    ADD CONSTRAINT fk_role FOREIGN KEY (role_id) REFERENCES tasklist.role_data(id) ON UPDATE RESTRICT ON DELETE RESTRICT NOT VALID;
 =   ALTER TABLE ONLY tasklist.user_role DROP CONSTRAINT fk_role;
       tasklist          postgres    false    207    2910    215            n           2606    16768    stat fk_stat_user    FK CONSTRAINT     �   ALTER TABLE ONLY tasklist.stat
    ADD CONSTRAINT fk_stat_user FOREIGN KEY (user_id) REFERENCES tasklist.user_data(id) ON UPDATE RESTRICT ON DELETE CASCADE NOT VALID;
 =   ALTER TABLE ONLY tasklist.stat DROP CONSTRAINT fk_stat_user;
       tasklist          postgres    false    213    2920    209            o           2606    16773    task fk_task_category    FK CONSTRAINT     �   ALTER TABLE ONLY tasklist.task
    ADD CONSTRAINT fk_task_category FOREIGN KEY (category_id) REFERENCES tasklist.category(id) ON UPDATE RESTRICT ON DELETE SET NULL NOT VALID;
 A   ALTER TABLE ONLY tasklist.task DROP CONSTRAINT fk_task_category;
       tasklist          postgres    false    203    2906    211            p           2606    16778    task fk_task_priority    FK CONSTRAINT     �   ALTER TABLE ONLY tasklist.task
    ADD CONSTRAINT fk_task_priority FOREIGN KEY (priority_id) REFERENCES tasklist.priority(id) ON UPDATE RESTRICT ON DELETE SET NULL NOT VALID;
 A   ALTER TABLE ONLY tasklist.task DROP CONSTRAINT fk_task_priority;
       tasklist          postgres    false    211    205    2908            q           2606    16783    task fk_task_user    FK CONSTRAINT     �   ALTER TABLE ONLY tasklist.task
    ADD CONSTRAINT fk_task_user FOREIGN KEY (user_id) REFERENCES tasklist.user_data(id) ON UPDATE RESTRICT ON DELETE CASCADE NOT VALID;
 =   ALTER TABLE ONLY tasklist.task DROP CONSTRAINT fk_task_user;
       tasklist          postgres    false    213    211    2920            s           2606    16788    user_role fk_user    FK CONSTRAINT     �   ALTER TABLE ONLY tasklist.user_role
    ADD CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES tasklist.user_data(id) ON UPDATE CASCADE ON DELETE CASCADE NOT VALID;
 =   ALTER TABLE ONLY tasklist.user_role DROP CONSTRAINT fk_user;
       tasklist          postgres    false    213    215    2920            �      x������ � �      �      x������ � �      �      x������ � �             x�3�v�2�tt�������� ,��            x������ � �            x������ � �            x������ � �            x������ � �     